<template>
  <div>
    <p>{{ game?.type ?? 'no game' }}</p>
    <div v-if="game === null">
      <p>Loading...</p>
    </div>
    <LobbyScreen v-else-if="game.type === 'lobby'" :game/>
    <InProgressScreen v-else-if="game.type === 'game_in_progress'" :game/>
    <GameOverScreen v-else-if="game.type === 'game_over'" :game/>

    <KeybindDisplay v-if="game !== null && showKeybindDisplay" :game/>
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

  const key = event.key.toLowerCase();
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
        case 'awaiting_player_action_choice':
          if (key === ' ') {
            s({ type: 'skip' });
          }
          break;
        case 'awaiting_election_resolution':
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