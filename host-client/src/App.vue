<template>
  <div class="min-h-screen p-4">
    <AbilityUnlockNotification :game/>
    <ErrorToast v-model="errorMessage"/>

    <div class="max-w-6xl mx-auto min-h-screen flex flex-col">
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
  type AwaitingChiefActionChoiceInnerGameState,
  type AwaitingInvestigationAnalysisInnerGameState,
  CardConsequenceQualifier, type GameOverGameState,
  type GameState,
  type InProgressGameState,
  Role
} from '@/game/state.ts';
import LobbyScreen from '@/components/screens/LobbyScreen.vue';
import InProgressScreen from '@/components/screens/InProgressScreen.vue';
import GameOverScreen from '@/components/screens/GameOverScreen.vue';
import KeybindDisplay from '@/components/ui/KeybindDisplay.vue';
import ErrorToast from '@/components/ui/ErrorToast.vue';
import { useLocalStorage } from '@/util/use-local-storage.ts';
import { PlayerIcon } from '@/game/player-icon.ts';
import AbilityUnlockNotification from '@/components/ui/AbilityUnlockNotification.vue';

const ws = new WebsocketOwner(onMessage);
const game = ref<GameState | null>(null);
const errorMessage = ref<string | null>(null);

const showKeybindDisplay = useLocalStorage('show-keybind-display', true);

function onMessage(message: ServerInboundMessage) {
  console.log(message);
  if (message.type === 'update_game_state') {
    game.value = message.game_state;
  } else {
    let m = message.error.error_type;
    if (message.error.player_name) {
      m += ` (${message.error.player_name})`;
    }
    errorMessage.value = `${m}: ${message.error.message}`;
  }
}

(async function () {
  try {
    await navigator.wakeLock.request('screen');
  } catch (err) {
    console.error('failed to request wake lock', err);
  }

  setTimeout(() => PlayerIcon.preload());
  await ws.connect();
})();

onMounted(() => document.addEventListener('keydown', onKeyPress));
onUnmounted(() => document.removeEventListener('keydown', onKeyPress));

function onKeyPress(event: KeyboardEvent) {
  if (game.value === null) return;

  let key = event.code.toLowerCase();
  if (key.startsWith('key')) {
    key = event.key.toLowerCase();
  }

  if (debugKeys(key)) {
    event.preventDefault();
    return;
  }

  if (key === 'h') {
    event.preventDefault();
    showKeybindDisplay.value = !showKeybindDisplay.value;
    return;
  }

  if (key === 'f') {
    event.preventDefault();
    if (!document.fullscreenElement) {
      document.documentElement.requestFullscreen()
          .catch(err => console.error('Error attempting to enable full-screen mode', err));
    } else if (document.exitFullscreen) {
      document.exitFullscreen();
    }
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
        case 'awaiting_chief_card_discard':
        case 'awaiting_advisor_card_choice':
        case 'idle':
        case 'awaiting_investigation_analysis':
        case 'awaiting_chief_action_choice':
          if (key === 'space') {
            s({ type: 'skip' });
          }
          break;
        case 'awaiting_election_outcome':
          if (key === 'enter' || key === 'y') {
            s({ type: 'resolve_election', passed: true });
          } else if (key === 'backspace' || key === 'n') {
            s({ type: 'resolve_election', passed: false });
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

function debugKeys(key: string): boolean {
  if (key === 'l') {
    game.value = <InProgressGameState>{
      type: 'game_in_progress',
      players: [
        {
          name: 'joe biden',
          is_chief: true,
          is_alive: true,
          icon: 'raccoon',
          role: Role.ANGEL,
          was_advisor_last_round: false,
          was_chief_last_round: false,
        },
        {
          name: 'okay',
          is_chief: false,
          is_alive: true,
          icon: 'mouse',
          role: Role.SATAN,
          was_advisor_last_round: false,
          was_chief_last_round: true,
        },
        {
          name: 'meh',
          is_chief: false,
          is_alive: true,
          icon: 'cat',
          role: Role.DEMON,
          was_advisor_last_round: false,
          was_chief_last_round: false,
        },
        {
          name: 'jesus',
          is_chief: false,
          is_alive: true,
          icon: 'dog',
          role: Role.ANGEL,
          was_advisor_last_round: false,
          was_chief_last_round: false,
        }
      ],
      deck: {
        played_cards: [{
          consequence_qualifier: CardConsequenceQualifier.NEGATIVE,
          consequence: -1,
          description: 'okay',
          id: 1
        }, {
          consequence_qualifier: CardConsequenceQualifier.POSITIVE,
          consequence: 1,
          description: 'meh',
          id: 2
        }, {
          consequence_qualifier: CardConsequenceQualifier.NEUTRAL,
          consequence: 0,
          description: 'gaa',
          id: 3
        }]
      },
      failed_elections: 1,
      inner_game_state: <AwaitingInvestigationAnalysisInnerGameState>{
        target: 'jesus',
        type: 'awaiting_investigation_analysis',
      }
    };

    return true;
  }

  if (key === 'c') {
    const g = { ...(game.value as InProgressGameState) };
    g.inner_game_state = <AwaitingChiefActionChoiceInnerGameState>{
      type: 'awaiting_chief_action_choice',
      permitted_actions: ['KILL', 'ELECT']
    };
    game.value = g;
    return true;
  }

  if(key === 'o') {
    game.value = <GameOverGameState>{
      type: 'game_over',
      demons: ['meh'],
      satan: 'okay',
      reason: 'SATAN_KILLED',
      players: [{
        name: 'joe biden',
        icon: 'raccoon',
      }, {
        name: 'okay',
        icon: 'mouse',
      }, {
        name: 'meh',
        icon: 'cat',
      }, {
        name: 'jesus',
        icon: 'dog',
      }],
      winner: 'ANGELS'
    };

    return true;
  }

  return false;
}
</script>

<style>
body {
  background-color: var(--color-gray-50);
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

.toast-enter-active,
.toast-leave-active {
  transition: all 0.3s ease;
}

.toast-enter-from {
  opacity: 0;
  transform: translateX(30px);
}

.toast-leave-to {
  opacity: 0;
  transform: translateX(30px);
}
</style>