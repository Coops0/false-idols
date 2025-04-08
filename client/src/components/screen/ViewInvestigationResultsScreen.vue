<template>
  <div class="size-full flex flex-col items-center justify-center">

    <div v-if="!gameState.hasConfirmed" class="flex justify-center">
      <p class="text-gray-600 font-bold text-center">
        You are about to see if {{ gameState.player.name }} is a demon or angel. You cannot show this screen to
        anyone.
      </p>

      <BaseButton variant="primary" @click="() => emit('confirm')">
        Continue
      </BaseButton>
    </div>

    <div v-else class="flex justify-center text-center">
      <PlayerPreview
          :game
          :icon-variant="gameState.role === Role.DEMON ? 'demon' : 'angel'"
          :player="gameState.player"
          class="size-40 mx-auto"
      />
      <p :class="gameState.role === Role.DEMON ? 'text-red-500 font-bold' : 'font-medium text-blue-400'"
         class="text-base">
        {{ roleName(gameState.role) }}
      </p>

      <p class="text-xs text-gray-800 font-bold">You cannot show anyone this screen</p>

      <BaseButton class="w-full" variant="primary" @click="() => emit('confirm')">
        Continue
      </BaseButton>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { computed } from 'vue';
import type { Game, ViewInvestigationResultsGameState } from '@/game';
import { Role, roleName } from '@/game/messages.ts';
import BaseButton from '@/components/ui/BaseButton.vue';
import PlayerPreview from '@/components/ui/PlayerPreview.vue';

const emit = defineEmits<{ confirm: []; }>();
const props = defineProps<{ game: Game; }>();
const gameState = computed(() => props.game.state as ViewInvestigationResultsGameState);
</script>