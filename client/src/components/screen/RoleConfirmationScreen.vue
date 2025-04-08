<template>
  <div class="size-full flex flex-col items-center justify-center text-center">

    <template v-if="!gameState.hasConfirmed">
      <p class="text-lg text-gray-600">You are about to see your secret role.</p>
      <p class="text-lg text-gray-700 font-semibold">Ensure nobody else can see your screen.</p>

      <BaseButton class="mt-14" variant="primary" @click="() => emit('confirm')">
        Okay
      </BaseButton>
    </template>

    <template v-else>
      <div>
        <PlayerPreview
            :icon-variant="roleToVariant(game.role)"
            :player="{ name: playerName, icon: playerIcon }"
        />
        <p class="text-4xl font-black mt-4" :class="game.role === Role.ANGEL ? 'text-blue-500' : 'text-red-500'">
          {{ roleName(game.role) }}</p>
      </div>

      <div class="mt-6 text-center space-y-4">
        <div class="space-y-3">
          <p v-if="game.role === Role.ANGEL" class="text-gray-700 text-sm">
            Play positive cards and try to eliminate all demons.
          </p>

          <div v-else-if="game.role === Role.DEMON">
            <p class="text-gray-700 text-xs">Subtly work together with the other demons and Satan to kill angels and
              play negative cards.</p>
            <p class="mt-4 font-medium text-sm text-gray-700">
              {{ gameState.isSmallGame ? 'Satan knows who you are.' : 'Satan does not know who the demons are.' }}</p>
          </div>

          <div v-else>
            <p class="text-gray-700 text-xs">Try to subtly work together with the other demons to pass negative cards,
              and kill
              angels.</p>
            <p class="mt-4 font-bold text-red-600 text-sm">If you die, the game ends immediately.</p>
          </div>
        </div>

        <div v-if="gameState.demons?.length" class="mt-10">
          <p class="text-lg mb-4 font-semibold text-gray-800">Your Team</p>

          <ul class="flex size-full flex-row flex-wrap justify-evenly gap-4">
            <li v-if="gameState.satan">
              <PlayerPreview :player="gameState.satan" icon-variant="satan"/>
              <p class="mt-4 text-xl font-bold text-red-500">SATAN</p>
            </li>
            <li v-for="demon in gameState.demons">
              <PlayerPreview :player="demon" icon-variant="demon"/>
              <p class="mt-4 text-xl font-bold text-red-500">DEMON</p>
            </li>
          </ul>
        </div>

        <p v-else-if="demonsText !== null" class="text-gray-700 text-sm">{{ demonsText }}</p>
      </div>

      <BaseButton class="mt-14" variant="primary" @click="() => emit('confirm')">
        Continue
      </BaseButton>
    </template>
  </div>
</template>

<script lang="ts" setup>
import type { Game, ViewRoleGameState } from '@/game';
import { computed } from 'vue';
import { Role, roleName } from '@/game/messages.ts';
import PlayerPreview from '@/components/ui/PlayerPreview.vue';
import { roleToVariant } from '@/util';
import BaseButton from '@/components/ui/BaseButton.vue';
import type { IconType } from '@/game/player-icon.ts';

const emit = defineEmits<{ confirm: []; }>();
const props = defineProps<{
  game: Game;
  playerName: string;
  playerIcon: IconType;
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