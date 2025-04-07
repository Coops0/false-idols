<template>
  <div class="size-full flex items-center justify-center">
      <div v-if="!gameState.hasConfirmed">
        <div class="text-center">
          <p class="text-gray-600 text-sm font-bold">
            You are about to see if {{ gameState.player.name }} is a demon or angel. You cannot show this screen to
            anyone.
          </p>
        </div>

        <div class="flex justify-center">
          <BaseButton variant="primary" @click="() => emit('confirm')">
            Continue
          </BaseButton>
        </div>
      </div>

      <div v-else>
        <div class="text-center space-y-1">
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
        </div>

        <div class="text-center">
          <p class="text-xs text-gray-800 font-bold">You cannot show anyone this screen</p>
        </div>

        <div class="flex justify-center">
          <BaseButton class="px-6 py-3 text-base" variant="primary" @click="() => emit('confirm')">
            Continue
          </BaseButton>
        </div>
      </div>
  </div>
</template>

<script lang="ts" setup>
import { computed } from 'vue';
import type { Game, ViewInvestigationResultsGameState } from '@/game';
import { Role, roleName } from '@/game/messages.ts';
import BaseButton from '@/components/ui/BaseButton.vue';
import BaseCard from '@/components/ui/BaseCard.vue';
import PlayerPreview from '@/components/ui/PlayerPreview.vue';

const emit = defineEmits<{ confirm: []; }>();
const props = defineProps<{ game: Game; }>();
const gameState = computed(() => props.game.state as ViewInvestigationResultsGameState);
</script>