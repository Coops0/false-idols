<template>
  <div>

  </div>
</template>

<script setup lang="ts">
import { WebsocketOwner } from '@/game/websocket-owner.ts';
import type { ServerInboundMessage } from '@/game/messages.ts';
import { ref } from 'vue';
import type { GameState } from '@/game/state.ts';

// TODO nosleep
const ws = new WebsocketOwner(onMessage);
const game = ref<GameState | null>(null);

function onMessage(message: ServerInboundMessage) {
  game.value = message.game_state;
}
</script>