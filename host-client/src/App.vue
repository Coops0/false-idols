<template>
  <div class="min-h-screen bg-gradient-to-br from-amber-50 to-amber-100 p-4 md:p-8">
    <div class="max-w-7xl mx-auto">
      <div class="relative">
        <div class="absolute inset-0 pointer-events-none">
          <div
              class="absolute top-0 left-0 w-64 h-64 bg-amber-200 rounded-full mix-blend-multiply filter blur-xl opacity-70 animate-blob"></div>
          <div
              class="absolute top-0 right-0 w-64 h-64 bg-amber-300 rounded-full mix-blend-multiply filter blur-xl opacity-70 animate-blob animation-delay-2000"></div>
          <div
              class="absolute -bottom-8 left-20 w-64 h-64 bg-amber-400 rounded-full mix-blend-multiply filter blur-xl opacity-70 animate-blob animation-delay-4000"></div>
        </div>

        <div class="relative">
          <div v-if="game === null" class="flex items-center justify-center min-h-[60vh]">
            <div class="text-center">
              <div
                  class="w-16 h-16 border-4 border-amber-400 border-t-transparent rounded-full animate-spin mx-auto"></div>
              <p class="mt-4 text-amber-800 text-lg">Loading...</p>
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

          <KeybindDisplay v-if="game !== null && showKeybindDisplay" :game class="fixed bottom-4 right-4"/>
        </div>
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
import { useLocalStorage } from '@/util/use-local-storage.ts';
import { PlayerIcon } from '@/game/player-icon.ts';

// TODO show animations/transitions in between in game states
// TODO show notifications when absolute card value unlocks investigation and kill

const ws = new WebsocketOwner(onMessage);
const game = ref<GameState | null>(null);

const showKeybindDisplay = useLocalStorage('show-keybind-display', true);

function onMessage(message: ServerInboundMessage) {
  console.log(message);
  game.value = message.game_state;
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

@keyframes blob {
  0% {
    transform: translate(0px, 0px) scale(1);
  }
  33% {
    transform: translate(30px, -50px) scale(1.1);
  }
  66% {
    transform: translate(-20px, 20px) scale(0.9);
  }
  100% {
    transform: translate(0px, 0px) scale(1);
  }
}

.animate-blob {
  animation: blob 7s infinite;
}

.animation-delay-2000 {
  animation-delay: 2s;
}

.animation-delay-4000 {
  animation-delay: 4s;
}
</style>