<template>
  <div class="size-full flex flex-col items-center justify-center text-center">
    <Transition mode="out-in" name="fade">
      <div v-if="!gameState.hasConfirmed" class="flex flex-col items-center">
        <p class="text-gray-600">You are about to see if {{ gameState.player.name }} is a demon or angel.</p>
        <div class="mt-14 mx-auto">
          <Alert type="warning" size="small">
            You cannot show anyone this screen
          </Alert>
        </div>

        <BaseButton class="mt-6" variant="primary" @click="() => emit('confirm')">
          Okay
        </BaseButton>
      </div>

      <div v-else class="flex flex-col items-center">
        <div>
          <PlayerPreview
              :icon-variant="gameState.role === Role.DEMON ? 'demon' : 'angel'"
              :player="gameState.player"
          />
          <p :class="gameState.role === Role.DEMON ? 'text-red-500' : 'text-blue-400'" class="mt-4 text-3xl font-bold">
            {{ roleName(gameState.role).toUpperCase() }}
          </p>
        </div>

        <div class="mt-10 mx-auto">
          <Alert type="warning" size="small">
            You cannot show anyone this screen
          </Alert>
        </div>

        <BaseButton class="mt-8" variant="primary" @click="() => emit('confirm')">
          Continue
        </BaseButton>
      </div>
    </Transition>
  </div>
</template>

<script lang="ts" setup>
import { computed } from 'vue';
import type { Game, ViewInvestigationResultsGameState } from '@/game';
import { Role, roleName } from '@/game/messages.ts';
import BaseButton from '@/components/ui/BaseButton.vue';
import PlayerPreview from '@/components/ui/PlayerPreview.vue';
import Alert from '@/components/ui/Alert.vue';

const emit = defineEmits<{ confirm: []; }>();
const props = defineProps<{ game: Game; }>();
const gameState = computed(() => props.game.state as ViewInvestigationResultsGameState);
</script>