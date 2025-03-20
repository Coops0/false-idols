<template>
  <div>
    <ul>
      <li v-for="action in gameState.permittedActions" :key="action">
        <button @click="() => swapAction(action)">{{ action }}</button>
      </li>
    </ul>
    <ul>
      <li v-for="player in players" :key="player.name">
        <PlayerPreview
            :player="player"
            icon-variant="normal"
            :disabled="!player.enabled"
            @click="() => commitPlayer(player.name)"
        />
      </li>
    </ul>
  </div>
</template>

<script lang="ts" setup>
import type { CommitActionGameState, Game } from '@/game';
import { computed, ref, watch } from 'vue';
import { ActionChoice, type ActionSupplementedPlayer } from '@/game/messages.ts';
import PlayerPreview from '@/components/ui/PlayerPreview.vue';

const props = defineProps<{ game: Game; }>();
const gameState = computed(() => props.game.state as CommitActionGameState);
const emit = defineEmits<{ commit: [playerName: string, action: ActionChoice] }>();

const selectedAction = ref(ActionChoice.NOMINATE);

watch(gameState, s => {
  selectedAction.value = s.permittedActions[0];
});

const players = computed<(ActionSupplementedPlayer & { enabled: boolean })[]>(() => gameState.value.supplementedPlayers
    .map(p => {
      const enabled =
          (p.electable && selectedAction.value === ActionChoice.NOMINATE) ||
          (p.investigatable && selectedAction.value === ActionChoice.INVESTIGATE) ||
          selectedAction.value === ActionChoice.KILL;

      return { ...p, enabled };
    }));

function swapAction(action: ActionChoice) {
  if (gameState.value.permittedActions.includes(action)) {
    selectedAction.value = action;
  }
}

function commitPlayer(playerName: string) {
  emit('commit', playerName, selectedAction.value);
}
</script>