<template>
  <div class="min-h-screen flex items-center justify-center bg-gradient-to-br from-gray-50 to-gray-100 p-4">
    <BaseCard class="w-full max-w-2xl">
      <div v-if="!gameState.hasConfirmed" class="space-y-6">
        <div class="text-center space-y-4">
          <h2 class="text-2xl font-bold text-gray-800">Important: Role Reveal</h2>
          <p class="text-gray-600 text-lg">
            We are about to reveal your secret role. Please ensure no other player can see your screen.
          </p>
        </div>

        <div class="flex justify-center">
          <BaseButton variant="primary" @click="() => emit('confirm')">
            I'm Ready
          </BaseButton>
        </div>
      </div>

      <div v-else class="space-y-8">
        <div class="flex justify-center">
          <PlayerPreview
              :icon-variant="roleToVariant(game.role)"
              :player="{ name: playerName, icon: playerIcon }"
          />
        </div>

        <div class="text-center space-y-4">
          <h2 class="text-3xl font-bold text-gray-800">
            Your Role: {{ roleName(game.role) }}
          </h2>

          <div class="bg-gray-50 rounded-lg p-6 space-y-4">
            <p v-if="game.role === Role.ANGEL" class="text-gray-700">
              Try to pass positive cards and eliminate any demons.
            </p>
            <p v-else-if="game.role === Role.DEMON" class="text-gray-700">
              You must work together with the other demons and try to pass negative cards, and kill innocent players.
              Satan does not know who the demons are.
            </p>
            <p v-else class="text-gray-700">
              The other demons know who you are, try to subtly work together to pass negative cards, and kill innocent
              players.
              <span class="font-bold text-red-600">If you die, the game ends immediately.</span>
            </p>
          </div>

          <div v-if="gameState.demonExtras" class="bg-gray-50 rounded-lg p-6 space-y-4">
            <h3 class="text-xl font-semibold text-gray-800">Your Team</h3>
            <div class="space-y-2">
              <p class="text-gray-700">Satan:</p>
              <PlayerPreview :player="gameState.demonExtras.satan" icon-variant="satan"/>
            </div>
            <div v-if="gameState.demonExtras.teammates.length" class="space-y-2">
              <p class="text-gray-700">Other Demons:</p>
              <ul class="space-y-2">
                <li v-for="demon in gameState.demonExtras.teammates">
                  <PlayerPreview :player="demon" icon-variant="demon"/>
                </li>
              </ul>
            </div>
          </div>

          <div v-else-if="demonsText !== null" class="bg-gray-50 rounded-lg p-6">
            <p class="text-gray-700">{{ demonsText }}</p>
          </div>
        </div>

        <div class="flex justify-center">
          <BaseButton variant="primary" @click="() => emit('confirm')">
            Continue
          </BaseButton>
        </div>
      </div>
    </BaseCard>
  </div>
</template>

<script lang="ts" setup>
import type { Game, ViewRoleGameState } from '@/game';
import { computed } from 'vue';
import { Role, roleName } from '@/game/messages.ts';
import PlayerPreview from '@/components/ui/PlayerPreview.vue';
import { roleToVariant } from '@/util';
import BaseButton from '@/components/ui/BaseButton.vue';
import BaseCard from '@/components/ui/BaseCard.vue';

const emit = defineEmits<{ confirm: []; }>();
const props = defineProps<{
  game: Game;
  playerName: string;
  playerIcon: string;
}>();
const gameState = computed(() => props.game.state as ViewRoleGameState);

const demonsText = computed(() => {
  const c = gameState.value.demonCount;
  if (c === 0) return null;
  if (c === 1) return 'There is 1 demon';
  return `There are ${c} demons`;
});
</script>