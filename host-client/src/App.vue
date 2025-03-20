<template>
  <div>
    <div v-if="game === null">
      <p>Loading...</p>
    </div>
    <LobbyScreen v-else-if="game.type === 'lobby'" :game="game"/>
    <InProgressScreen v-else-if="game.type === 'game_in_progress'" :game="game"/>
    <GameOverScreen v-else-if="game.type === 'game_over'" :game="game"/>
  </div>
</template>

<script setup lang="ts">
import { WebsocketOwner } from '@/game/websocket-owner.ts';
import type { ServerInboundMessage, ServerOutboundMessage } from '@/game/messages.ts';
import { onMounted, onUnmounted, ref } from 'vue';
import type { GameState } from '@/game/state.ts';
import LobbyScreen from '@/components/screens/LobbyScreen.vue';
import InProgressScreen from '@/components/screens/InProgressScreen.vue';
import GameOverScreen from '@/components/screens/GameOverScreen.vue';

const ws = new WebsocketOwner(onMessage);
const game = ref<GameState | null>(null);

function onMessage(message: ServerInboundMessage) {
  game.value = message.game_state;
}

(async function () {
  try {
    await navigator.wakeLock.request('screen');
  } catch (err) {
    console.error('failed to request wake lock', err);
  }
})();

onMounted(() => document.addEventListener('keydown', onKeyPress));
onUnmounted(() => document.removeEventListener('keydown', onKeyPress));

function onKeyPress(event: KeyboardEvent) {
  if (game.value === null) return;

  const s = (message: ServerOutboundMessage) => {
    ws.send(message);
    event.preventDefault();
  };

  switch (game.value.type) {
    case 'lobby':
      if (event.key === 'Enter') {
        s({ type: 'start_game' });
      } else if (event.key === 'Escape') {
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
          if (event.key === ' ') {
            s({ type: 'skip' });
          }
          break;
        case 'awaiting_election_resolution':
          if (event.key === 'Enter' || event.key === 'y') {
            s({ type: 'resolve_election', passed: true });
          } else if (event.key === 'Backspace' || event.key === 'n') {
            s({ type: 'resolve_election', passed: false });
          }
          break;
      }
      break;
    case 'game_over':
      if (event.key === 'Enter') {
        s({ type: 'go_back_to_lobby' });
      }
      break;
  }
}
</script>