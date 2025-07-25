<template>
  <div class="size-full flex flex-col items-center justify-center gap-y-14">
    <p class="text-xl font-bold text-gray-800 text-center">Choose who to <span
        :class="actionColor" class="font-bold">{{ actionName }}</span></p>

    <div class="flex flex-row flex-wrap justify-evenly gap-6 w-full">
      <PlayerPreview
          v-for="player in players"
          :key="player.name"
          :class="!player.enabled && 'opacity-50'"
          :player
          @click="() => player.enabled && commitPlayer(player.name)"
      />
    </div>
  </div>
</template>

<script lang="ts" setup>
import type { CommitActionGameState, Game } from '@/game';
import { computed } from 'vue';
import { ActionChoice, type ActionSupplementedPlayer } from '@/game/messages.ts';
import PlayerPreview from '@/components/ui/PlayerPreview.vue';

const props = defineProps<{ game: Game; }>();
const gameState = computed(() => props.game.state as CommitActionGameState);
const emit = defineEmits<{ commit: [playerName: string] }>();

const players = computed<(ActionSupplementedPlayer & { enabled: boolean })[]>(() => {
  const action = gameState.value.action;
  return gameState.value.supplementedPlayers
      .map(p => {
        const enabled =
            (p.electable && action === ActionChoice.NOMINATE) ||
            (p.investigatable && action === ActionChoice.INVESTIGATE) ||
            action === ActionChoice.KILL ||
            action === ActionChoice.NOMINATE_PRESIDENT;

        return { ...p, enabled };
      });
});

const actionName = computed(() => {
  switch (gameState.value.action) {
    case ActionChoice.INVESTIGATE:
      return 'INVESTIGATE';
    case ActionChoice.KILL:
      return 'KILL';
    case ActionChoice.NOMINATE:
      return 'NOMINATE';
    case ActionChoice.NOMINATE_PRESIDENT:
      return 'NOMINATE PRESIDENT';
  }
});

const actionColor = computed(() => {
  switch (gameState.value.action) {
    case ActionChoice.INVESTIGATE:
      return 'text-blue-500';
    case ActionChoice.KILL:
      return 'text-red-500';
    case ActionChoice.NOMINATE:
      return 'text-gray-400';
    case ActionChoice.NOMINATE_PRESIDENT:
      return 'text-gray-600';
  }
});

function commitPlayer(playerName: string) {
  emit('commit', playerName);
}
</script>