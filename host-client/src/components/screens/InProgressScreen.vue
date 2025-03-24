<template>
  <div>
    <ul>
      <li v-for="player in game.players" :key="player.name">
        <PlayerPreview :player="player" :icon-variant="player.is_alive ? 'normal' : 'normal'"/>
      </li>
    </ul>

    <AwaitingAdvisorCardChoiceScreen v-if="game.inner_game_state.type === 'awaiting_advisor_card_choice'" :game/>
    <AwaitingChiefCardDiscardScreen v-else-if="game.inner_game_state.type === 'awaiting_chief_card_discard'" :game/>
    <AwaitingElectionResolutionScreen v-else-if="game.inner_game_state.type === 'awaiting_election_resolution'" :game/>
    <AwaitingInvestigationAnalysis v-else-if="game.inner_game_state.type === 'awaiting_investigation_analysis'" :game/>
    <AwaitingPlayerActionChoiceScreen v-else-if="game.inner_game_state.type === 'awaiting_player_action_choice'" :game/>
    <IdleScreen v-else-if="game.inner_game_state.type === 'idle'" :game/>
  </div>
</template>

<script setup lang="ts">
import type { InProgressGameState } from '@/game/state.ts';
import PlayerPreview from '@/components/ui/PlayerPreview.vue';
import AwaitingAdvisorCardChoiceScreen from '@/components/screens/game-screens/AwaitingAdvisorCardChoiceScreen.vue';
import AwaitingChiefCardDiscardScreen from '@/components/screens/game-screens/AwaitingChiefCardDiscardScreen.vue';
import AwaitingElectionResolutionScreen from '@/components/screens/game-screens/AwaitingElectionResolutionScreen.vue';
import AwaitingInvestigationAnalysis from '@/components/screens/game-screens/AwaitingInvestigationAnalysis.vue';
import AwaitingPlayerActionChoiceScreen from '@/components/screens/game-screens/AwaitingPlayerActionChoiceScreen.vue';
import IdleScreen from '@/components/screens/game-screens/IdleScreen.vue';

const props = defineProps<{ game: InProgressGameState }>();
</script>