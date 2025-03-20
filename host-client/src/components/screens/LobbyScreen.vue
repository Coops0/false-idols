<template>
  <div>
    <ul>
      <li v-for="player in game.players" :key="player.name">
        <PlayerPreview :player="player" icon-variant="normal"/>
      </li>
    </ul>
  </div>
</template>

<script setup lang="ts">
import type { LobbyGameState } from '@/game/state.ts';
import PlayerPreview from '@/components/ui/PlayerPreview.vue';

defineProps<{ game: LobbyGameState }>();
</script>