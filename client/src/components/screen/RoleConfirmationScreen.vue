<template>
  <div>
    <div v-if="!gameState.hasConfirmed">
      <p>We are about to reveal your secret role, please ensure no other player can see your screen.</p>
      <button @click="() => emit('confirm')">Confirm</button>
    </div>
    <div v-else>
      <PlayerPreview
          :player="{ name: playerName, icon: playerIcon }"
          :icon-variant="roleToVariant(game.role)"
      />
      <p>Your role is: {{ game.role }}</p>
      <p v-if="game.role === Role.ANGEL">
        Try to pass positive cards and eliminate any demons.
      </p>
      <p v-else-if="game.role === Role.DEMON">
        You must work together with the other demons and try to pass negative cards, and kill innocent players.
        Satan does not know who the demon are.
      </p>
      <p v-else>
        The other demons know who you are, try to subtly work together to pass negative cards, and kill innocent
        players.
        <span class="bold">If you die, the game ends immediately.</span>
      </p>
      <div v-if="gameState.demonExtras">
        <p>Your teammates:</p>
        <p>Satan:
          <PlayerPreview :player="gameState.demonExtras.satan" icon-variant="satan"/>
        </p>
        <ul v-if="gameState.demonExtras.teammates.length">
          <li v-for="demon in gameState.demonExtras.teammates">
            <PlayerPreview :player="demon" icon-variant="demon"/>
          </li>
        </ul>
      </div>
      <div v-else-if="demonsText !== null">
        <p>{{ demonsText }}</p>
      </div>
      <button @click="() => emit('confirm')">Continue</button>
    </div>
  </div>
</template>

<script lang="ts" setup>
import type { Game, ViewRoleGameState } from '@/game';
import { computed } from 'vue';
import { Role } from '@/game/messages.ts';
import PlayerPreview from '@/components/ui/PlayerPreview.vue';
import { roleToVariant } from '@/util';

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