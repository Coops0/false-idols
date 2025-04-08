<template>
  <div class="w-full h-screen overflow-hidden">
    <div class="mx-auto h-full flex flex-col">
      <div class="relative flex-1">
        <div v-if="game === null" class="flex items-center justify-center h-full">
          <div class="text-center">
            <div class="w-16 h-16 border-4 border-gray-200 border-t-blue-500 rounded-full animate-spin mx-auto"/>
            <p class="mt-4 text-gray-600 text-lg font-medium">Loading...</p>
          </div>
        </div>

        <Transition v-else mode="out-in" name="fade">
          <template v-if="game.type === 'lobby'">
            <LobbyScreen :game/>
          </template>
          <template v-else-if="game.type === 'game_in_progress'">
            <InProgressScreen :game/>
          </template>
          <template v-else-if="game.type === 'game_over'">
            <GameOverScreen :game/>
          </template>
        </Transition>

        <Transition mode="out-in" name="fade">
          <KeybindDisplay v-if="game !== null && showKeybindDisplay" :game class="fixed bottom-4 right-4"/>
        </Transition>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { WebsocketOwner } from '@/game/websocket-owner.ts';
import type { ServerInboundMessage, ServerOutboundMessage } from '@/game/messages.ts';
import { onMounted, onUnmounted, ref } from 'vue';
import {
  type AwaitingInvestigationAnalysisInnerGameState,
  type AwaitingPresidentActionChoiceInnerGameState,
  CardConsequence,
  type GameOverGameState,
  type GameState,
  type InProgressGameState
} from '@/game/state.ts';
import LobbyScreen from '@/components/screens/LobbyScreen.vue';
import InProgressScreen from '@/components/screens/InProgressScreen.vue';
import GameOverScreen from '@/components/screens/GameOverScreen.vue';
import KeybindDisplay from '@/components/ui/KeybindDisplay.vue';
import { useLocalStorage } from '@/util/use-local-storage.ts';
import { toast } from 'vue3-toastify';
import { preloadImages } from '@/util/preload-images.util.ts';

const ws = new WebsocketOwner(onMessage, shouldRequestState);
const game = ref<GameState | null>(null);

const showKeybindDisplay = useLocalStorage('show-keybind-display', true);

function onMessage(message: ServerInboundMessage) {
  console.log(message);
  switch (message.type) {
    case 'update_game_state':
      game.value = message.game_state;
      break;
    case 'policy_peeking':
      const g = game.value;
      if (g === null || g.type !== 'game_in_progress') return;

      const presidentName = g.players.find(p => p.is_president)?.name;
      toast(`${presidentName} is policy peeking`, {
        type: toast.TYPE.INFO,
        position: toast.POSITION.TOP_CENTER,
        autoClose: 6000
      });
      break;
    case 'error':
      let m = message.error.error_type;
      if (message.error.player_name) {
        m += ` (${message.error.player_name})`;
      }

      toast(`${m}: ${message.error.message}`, {
        type: toast.TYPE.ERROR,
        position: toast.POSITION.TOP_RIGHT,
        autoClose: 3000,
        closeOnClick: true,
        pauseOnHover: true,
      });
      break;
  }
}

function shouldRequestState(): boolean {
  if (game.value === null) {
    return true;
  } else {
    return !game.value.type?.length;
  }
}

(async function () {
  try {
    await navigator.wakeLock.request('screen');
  } catch (err) {
    console.error('failed to request wake lock', err);
  }

  await ws.connect();
})();

onMounted(() => {
  document.addEventListener('keydown', onKeyPress);
  setTimeout(() => preloadImages());
});

onUnmounted(() => document.removeEventListener('keydown', onKeyPress));

function onKeyPress(event: KeyboardEvent) {
  if (game.value === null) return;

  let key = event.code.toLowerCase();
  if (key.startsWith('key')) {
    key = event.key.toLowerCase();
  }

  if (key === 'h') {
    event.preventDefault();
    showKeybindDisplay.value = !showKeybindDisplay.value;
    return;
  }

  if (key === 'f') {
    event.preventDefault();
    if (!document.fullscreenElement) {
      document.documentElement
          .requestFullscreen()
          .catch(err => console.error('Error attempting to enable full-screen mode', err));
    } else if (document.exitFullscreen) {
      document.exitFullscreen();
    }
    return;
  }

  if (key === 'altleft' || key === 'altright') {
    event.preventDefault();
    document.body.classList.toggle('no-cursor');
    return;
  }

  if (key === 'l') {
    game.value = <InProgressGameState>{
      type: 'game_in_progress',
      inner_game_state: <AwaitingPresidentActionChoiceInnerGameState>{
        type: 'awaiting_president_action_choice',
        action: 'KILL'
      },
      players: [
        {
          name: 'Dog',
          role: 'ANGEL',
          icon: 'bear',
          was_advisor_last_round: false,
          was_president_last_round: false,
          is_president: true,
          is_alive: true
        },
        {
          name: 'Cat',
          role: 'SATAN',
          icon: 'panda',
          was_advisor_last_round: false,
          was_president_last_round: false,
          is_president: false,
          is_alive: true
        },
        {
          name: 'Fish',
          role: 'DEMON',
          icon: 'pig',
          was_advisor_last_round: false,
          was_president_last_round: false,
          is_president: false,
          is_alive: true
        },
        {
          name: 'Bird',
          role: 'ANGEL',
          icon: 'mouse',
          was_advisor_last_round: false,
          was_president_last_round: false,
          is_president: false,
          is_alive: true
        }
      ],
      deck: {
        played_cards: [{
          consequence: CardConsequence.POSITIVE,
          id: 1,
          description: 'Card 1 woohoo'
        },
          {
            consequence: CardConsequence.NEGATIVE,
            id: 2,
            description: 'Card 2 boohoo'
          },
          {
            consequence: CardConsequence.POSITIVE,
            id: 3,
            description: 'Card 3 woohoo'
          },
          {
            consequence: CardConsequence.NEGATIVE,
            id: 4,
            description: 'Card 4 boohoo'
          }],
        card_stack: []
      },
      failed_elections: 1
    };
    return;
  }

  if (key === 'o') {
    game.value = <GameOverGameState>{
      type: 'game_over',
      winner: 'ANGELS',
      reason: 'SATAN_KILLED',
      players: [{
        name: 'Dog',
        icon: 'bear'
      },
        {
          name: 'Cat',
          icon: 'panda'
        },
        {
          name: 'Fish',
          icon: 'pig'
        },
        {
          name: 'Bird',
          icon: 'mouse'
        }],
      demons: ['Fish', 'Dog', 'Bird', 'Cat'],
      satan: 'Cat',
    };
    return;
  }

  if (key === 'k') {
    const s = { ...game.value } as InProgressGameState;
    s.inner_game_state = <AwaitingInvestigationAnalysisInnerGameState>{
      type: 'awaiting_investigation_analysis',
      target: 'Dog',
    };
    game.value = s;
    return;
  }

  const s = (message: ServerOutboundMessage) => {
    ws.send(message);
    event.preventDefault();
  };

  switch (game.value.type) {
    case 'lobby':
      if (key === 'enter') {
        s({ type: 'start_game' });
      } else if (key === 'escape') {
        s({ type: 'reset_players' });
      }
      break;
    case 'game_in_progress':
      switch (game.value.inner_game_state.type) {
        case 'awaiting_president_card_discard':
        case 'idle':
        case 'awaiting_investigation_analysis':
        case 'awaiting_president_action_choice':
          if (key === 'space') {
            s({ type: 'skip' });
          }
          break;
        case 'awaiting_advisor_election_outcome':
        case 'awaiting_president_election_outcome':
          if (key === 'enter' || key === 'y') {
            s({ type: 'resolve_election', passed: true });
          } else if (key === 'backspace' || key === 'n') {
            s({ type: 'resolve_election', passed: false });
          }
          break;
        case 'awaiting_advisor_card_choice':
          if (key === 'backspace' || key === 'v') {
            s({ type: 'veto' });
          }
          break;
      }
      break;
    case 'game_over':
      if (key === 'enter') {
        s({ type: 'go_back_to_lobby' });
      }
      break;
  }
}
</script>

<style>
body {
  background-color: var(--color-gray-50);
}

.no-cursor {
  cursor: none;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>