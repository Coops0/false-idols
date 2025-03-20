<template>
  <div>
    <div v-if="!gameState.hasConfirmed">
      <p>We are about to reveal your secret role, please ensure no other player can see your screen.</p>
      <button @click="() => emit('confirm')">Confirm</button>
    </div>
    <div v-else>
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
        <p>Satan: {{ gameState.demonExtras.satan.name }}</p>
        <ul v-if="gameState.demonExtras.teammates.length">
          <li v-for="demon in gameState.demonExtras.teammates">{{ demon.name }}</li>
        </ul>
      </div>
      <div v-else>
        <p>There are {{ gameState.demonCount }} demon{{ gameState.demonCount == 1 ? '' : 's' }}</p>
      </div>
      <button @click="() => emit('confirm')">Continue</button>
    </div>
  </div>
</template>

<script lang="ts" setup>
import type { Game, ViewRoleGameState } from '@/game';
import { computed } from 'vue';
import { Role } from '@/game/messages.ts';

const emit = defineEmits<{ confirm: []; }>();
const props = defineProps<{ game: Game; }>();
const gameState = computed(() => props.game.state as ViewRoleGameState);
</script>