<template>
  <div class="min-h-screen flex items-center justify-center bg-gradient-to-br from-gray-50 to-gray-100 p-4">
    <BaseCard class="w-full max-w-4xl mx-4">
      <template #header>
        <h1 class="text-xl md:text-2xl font-bold text-gray-800 text-center">Do Something</h1>
      </template>

      <div class="space-y-6 md:space-y-8">
        <div class="flex flex-col sm:flex-row justify-center gap-2 sm:gap-4">
          <button
              v-for="action in [ActionChoice.NOMINATE, ActionChoice.INVESTIGATE, ActionChoice.KILL]"
              :key="action"
              :class="{
              'bg-blue-500 text-white shadow-lg': selectedAction === action,
              'opacity-50': !isValidAction(action),
              'bg-gray-100 text-gray-700': selectedAction !== action
            }"
              class="px-4 sm:px-6 py-3 rounded-lg font-semibold transition-all duration-200 text-sm sm:text-base"
              @click="() => swapAction(action)"
          >
            {{ action }}
          </button>
        </div>

        <div class="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 gap-3 sm:gap-4">
          <div
              v-for="player in players"
              :key="player.name"
              :class="!player.enabled && 'opacity-50'"
              class="transition-all duration-200 cursor-pointer active:scale-95"
              @click="() => player.enabled && commitPlayer(player.name)"
          >
            <PlayerPreview
                :player="player"
                icon-variant="normal"
            />
          </div>
        </div>
      </div>
    </BaseCard>
  </div>
</template>

<script lang="ts" setup>
import type { CommitActionGameState, Game } from '@/game';
import { computed, ref, watch } from 'vue';
import { ActionChoice, type ActionSupplementedPlayer } from '@/game/messages.ts';
import PlayerPreview from '@/components/ui/PlayerPreview.vue';
import BaseCard from '@/components/ui/BaseCard.vue';

const props = defineProps<{ game: Game; }>();
const gameState = computed(() => props.game.state as CommitActionGameState);
const emit = defineEmits<{ commit: [playerName: string, action: ActionChoice] }>();

const selectedAction = ref(ActionChoice.NOMINATE);

watch(gameState, s => {
  selectedAction.value = s.permittedActions[0];
});

const players = computed<(ActionSupplementedPlayer & { enabled: boolean })[]>(() => gameState.value.supplementedPlayers
    .map(p => {
      const enabled =
          (p.electable && selectedAction.value === ActionChoice.NOMINATE) ||
          (p.investigatable && selectedAction.value === ActionChoice.INVESTIGATE) ||
          selectedAction.value === ActionChoice.KILL;

      return { ...p, enabled };
    }));

function isValidAction(action: ActionChoice) {
  return gameState.value.permittedActions.includes(action);
}

function swapAction(action: ActionChoice) {
  if (isValidAction(action)) {
    selectedAction.value = action;
  }
}

function commitPlayer(playerName: string) {
  emit('commit', playerName, selectedAction.value);
}
</script>