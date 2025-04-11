<template>
  <div class="w-full h-screen overflow-hidden">
    <PreloadImages/>
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
            <LobbyScreen :game @kick="playerName => kick(playerName)"/>
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
import { type GameState } from '@/game/state.ts';
import LobbyScreen from '@/components/screens/LobbyScreen.vue';
import InProgressScreen from '@/components/screens/InProgressScreen.vue';
import GameOverScreen from '@/components/screens/GameOverScreen.vue';
import KeybindDisplay from '@/components/ui/KeybindDisplay.vue';
import { useLocalStorage } from '@/util/use-local-storage.ts';
import { toast } from 'vue3-toastify';
import { setupWakeLock } from '@/util/wake-lock.util.ts';
import PreloadImages from '@/components/PreloadImages.vue';

const ws = new WebsocketOwner(onMessage, shouldRequestState);
const game = ref<GameState | null>(null);

const showKeybindDisplay = useLocalStorage('show-keybind-display', true);

function onMessage(message: ServerInboundMessage) {
  console.log(message);
  switch (message.type) {
    case 'shutdown':
      ws.close();
      game.value = null;
      break;
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

onMounted(() => {
  setupWakeLock();
  ws.connect();

  document.addEventListener('keydown', onKeyPress);
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
      if (key === 'escape') {
        s({ type: 'end_game' });
        return;
      }

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

function kick(playerName: string) {
  ws.send({ type: 'kick', player_name: playerName });
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
</style>