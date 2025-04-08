<template>
  <div class="size-full flex flex-col items-center justify-center text-center">

    <template v-if="!gameState.hasConfirmed">
      <p class="text-gray-600">You are about to see if {{ gameState.player.name }} is a demon or angel.</p>
      <p class="text-gray-700 font-semibold">You cannot show this screen to anyone.</p>

      <BaseButton class="mt-14" variant="primary" @click="() => emit('confirm')">
        Okay
      </BaseButton>
    </template>

    <template v-else>
      <div>
        <PlayerPreview
            :icon-variant="gameState.role === Role.DEMON ? 'demon' : 'angel'"
            :player="gameState.player"
        />
        <p class="mt-4 text-3xl font-bold" :class="gameState.role === Role.DEMON ? 'text-red-500' : 'text-blue-400'">
          {{ roleName(gameState.role).toUpperCase() }}
        </p>
      </div>

      <p class="mt-14 text-xs text-gray-800 font-bold">You cannot show anyone this screen</p>

      <BaseButton class="mt-8" variant="primary" @click="() => emit('confirm')">
        Continue
      </BaseButton>
    </template>
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