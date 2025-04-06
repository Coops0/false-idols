<template>
  <div class="min-h-screen flex items-center justify-center p-4">
    <BaseCard class="w-full max-w-2xl mx-4">
      <div v-if="!gameState.hasConfirmed" class="space-y-6">
        <div class="text-center space-y-4">
          <h2 class="text-xl md:text-2xl font-bold text-gray-800">Important</h2>
          <p class="text-gray-600 text-sm md:text-base">
            You are about to see your secret role. Ensure nobody else can see your screen. Do not show it to anyone.
          </p>
        </div>

        <div class="flex justify-center">
          <BaseButton class="px-6 py-3 text-base" variant="primary" @click="() => emit('confirm')">
            Continue
          </BaseButton>
        </div>
      </div>

      <div v-else class="space-y-6 md:space-y-8">
        <div class="flex justify-center">
          <PlayerPreview
              :icon-variant="roleToVariant(game.role)"
              :player="{ name: playerName, icon: playerIcon }"
          />
        </div>

        <div class="text-center space-y-4">
          <h2 class="text-2xl md:text-3xl font-bold text-gray-800">
            Your Role: {{ roleName(game.role) }}
          </h2>

          <div class="bg-gray-50 rounded-lg p-4 md:p-6 space-y-3 md:space-y-4">
            <p v-if="game.role === Role.ANGEL" class="text-gray-700 text-sm md:text-base">
              Play positive cards and try to eliminate all demons.
            </p>
            <p v-else-if="game.role === Role.DEMON" class="text-gray-700 text-sm md:text-base">
              Work together with the other demons/Satan to kill angels and play negative cards.
              {{ gameState.isSmallGame ? 'Satan knows who you are.' : 'Satan does not know who the demons are.' }}
            </p>
            <p v-else class="text-gray-700 text-sm md:text-base">
              Try to subtly work together with the other demons to pass negative cards, and kill angels.
              <span class="font-bold text-red-600">If you die, the game ends immediately.</span>
            </p>
          </div>

          <div v-if="gameState.demons?.length" class="bg-gray-50 rounded-lg p-4 md:p-6 space-y-3 md:space-y-4">
            <h3 class="text-lg md:text-xl font-semibold text-gray-800">Your Team</h3>
            <div class="space-y-2" v-if="gameState.satan">
              <p class="text-gray-700 text-sm md:text-base">Satan:</p>
              <PlayerPreview :player="gameState.satan" icon-variant="satan"/>
            </div>
            <div v-if="gameState.demons.length" class="space-y-2">
              <p class="text-gray-700 text-sm md:text-base">{{ game.role === Role.DEMON ? 'Other ' : '' }}Demons:</p>
              <ul class="space-y-2">
                <li v-for="demon in gameState.demons">
                  <PlayerPreview :player="demon" icon-variant="demon"/>
                </li>
              </ul>
            </div>
          </div>

          <div v-else-if="demonsText !== null" class="bg-gray-50 rounded-lg p-4 md:p-6">
            <p class="text-gray-700 text-sm md:text-base">{{ demonsText }}</p>
          </div>
        </div>

        <div class="flex justify-center">
          <BaseButton class="px-6 py-3 text-base" variant="primary" @click="() => emit('confirm')">
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

  const other = props.game.role === Role.SATAN ? ' other ' : ' ';
  const includingSatan = props.game.role === Role.SATAN ? ' (not including Satan)' : '';

  if (c === 1) {
    return `There is 1${other}demon${includingSatan}`;
  } else {
    return `There are ${c}${other}demons${includingSatan}`;
  }
});
</script>