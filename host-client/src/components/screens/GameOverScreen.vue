<template>
  <div>
    <p>{{ roleName(game.winner as Role) }}</p>
    <p>{{ reasonText }}</p>

    <div v-if="demons.length">
      <p>Demons</p>
      <ul>
        <li v-for="player in demons" :key="player.name">
          <PlayerPreview :player="player" icon-variant="demon"/>
        </li>
      </ul>
    </div>
    <div>
      <p>Satan</p>
      <PlayerPreview :player="satan" icon-variant="satan"/>
    </div>
  </div>
</template>

<script setup lang="ts">
import { type GameOverGameState, Role, roleName } from '@/game/state.ts';
import { computed } from 'vue';
import PlayerPreview from '@/components/ui/PlayerPreview.vue';

const props = defineProps<{ game: GameOverGameState }>();

const reasonText = computed(() => {
  switch (props.game.reason) {
    case 'NEGATIVE_THRESHOLD_REACHED':
      return 'The demons played enough negative cards';
    case 'POSITIVE_THRESHOLD_REACHED':
      return 'The angels played enough positive cards';
    case 'ALL_ANGELS_DEAD':
      return 'All angels were killed';
    case 'SATAN_ELECTED_ADVISOR_LATE_GAME':
      return 'Satan was elected as advisor';
    case 'SATAN_KILLED':
      return 'Satan was killed';
  }
});

const demons = computed(() => props.game.demons.map(d => props.game.players.find(p => p.name === d)!));
const satan = computed(() => props.game.players.find(p => p.name === props.game.satan)!);
</script>