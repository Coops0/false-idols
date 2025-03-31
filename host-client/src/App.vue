<template>
  <div class="min-h-screen bg-gray-50 p-4">
    <AbilityUnlockNotification :game/>
    <ErrorToast v-model="errorMessage"/>

    <div class="max-w-6xl mx-auto h-screen flex flex-col">
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
import type { GameState } from '@/game/state.ts';
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
</script>

<style>
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