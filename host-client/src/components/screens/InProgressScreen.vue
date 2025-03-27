<template>
  <div class="space-y-8">
    <DivineCard>
      <div class="space-y-4">
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          <PlayerCard v-for="player in game.players" :key="player.name" :player/>
        </div>
      </div>
    </DivineCard>

    <Transition mode="out-in" name="fade">
      <AwaitingAdvisorCardChoiceScreen v-if="game.inner_game_state.type === 'awaiting_advisor_card_choice'"
                                       :game/>
      <AwaitingChiefCardDiscardScreen v-else-if="game.inner_game_state.type === 'awaiting_chief_card_discard'"
                                      :game/>
      <AwaitingElectionResolutionScreen v-else-if="game.inner_game_state.type === 'awaiting_election_resolution'"
                                        :game/>
      <AwaitingInvestigationAnalysis v-else-if="game.inner_game_state.type === 'awaiting_investigation_analysis'"
                                     :game/>
      <AwaitingPlayerActionChoiceScreen v-else-if="game.inner_game_state.type === 'awaiting_chief_action_choice'"
                                        :game/>
      <IdleScreen v-else-if="game.inner_game_state.type === 'idle'" :game/>
    </Transition>
    
    <ScoreBar :game/>
    <ChaosBar :game/>
  </div>
</template>

<script lang="ts" setup>
import PlayerCard from '@/components/ui/PlayerCard.vue';
import DivineCard from '@/components/ui/DivineCard.vue';
import AwaitingAdvisorCardChoiceScreen from '@/components/screens/game-screens/AwaitingAdvisorCardChoiceScreen.vue';
import AwaitingChiefCardDiscardScreen from '@/components/screens/game-screens/AwaitingChiefCardDiscardScreen.vue';
import AwaitingElectionResolutionScreen from '@/components/screens/game-screens/AwaitingElectionResolutionScreen.vue';
import AwaitingInvestigationAnalysis from '@/components/screens/game-screens/AwaitingInvestigationAnalysis.vue';
import AwaitingPlayerActionChoiceScreen from '@/components/screens/game-screens/AwaitingPlayerActionChoiceScreen.vue';
import IdleScreen from '@/components/screens/game-screens/IdleScreen.vue';
import type { InProgressGameState } from '@/game/state.ts';
import ScoreBar from '@/components/ui/ScoreBar.vue';
import ChaosBar from '@/components/ui/ChaosBar.vue';

defineProps<{ game: InProgressGameState }>();
</script>

<style scoped>
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>