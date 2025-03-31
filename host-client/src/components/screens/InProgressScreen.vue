<template>
  <div class="h-full flex flex-col">
    <div class="min-h-48 mb-4">
      <ModernCard class="h-full">
        <div class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4 h-full">
          <PlayerCard v-for="player in game.players" :key="player.name" :player/>
        </div>
      </ModernCard>
    </div>

    <div class="h-64 relative">
      <div class="absolute inset-0">
        <AwaitingAdvisorCardChoiceScreen v-if="game.inner_game_state.type === 'awaiting_advisor_card_choice'"
                                         :game/>
        <AwaitingChiefCardDiscardScreen v-else-if="game.inner_game_state.type === 'awaiting_chief_card_discard'"
                                        :game/>
        <AwaitingElectionOutcomeScreen v-else-if="game.inner_game_state.type === 'awaiting_election_outcome'"
                                       :game/>
        <AwaitingInvestigationAnalysis v-else-if="game.inner_game_state.type === 'awaiting_investigation_analysis'"
                                       :game/>
        <AwaitingPlayerActionChoiceScreen v-else-if="game.inner_game_state.type === 'awaiting_chief_action_choice'"
                                          :game/>
        <IdleScreen v-else-if="game.inner_game_state.type === 'idle'" :game/>
      </div>
    </div>

    <div class="h-24 mt-4">
      <div class="grid grid-cols-3 gap-4 h-full">
        <CardDeck :game class="h-full"/>
        <ScoreBar :game class="h-full"/>
        <ChaosBar v-if="game.failed_elections > 0" :game class="h-full"/>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import PlayerCard from '@/components/ui/PlayerCard.vue';
import ModernCard from '@/components/ui/ModernCard.vue';
import AwaitingAdvisorCardChoiceScreen from '@/components/screens/game-screens/AwaitingAdvisorCardChoiceScreen.vue';
import AwaitingChiefCardDiscardScreen from '@/components/screens/game-screens/AwaitingChiefCardDiscardScreen.vue';
import AwaitingElectionOutcomeScreen from '@/components/screens/game-screens/AwaitingElectionOutcomeScreen.vue';
import AwaitingInvestigationAnalysis from '@/components/screens/game-screens/AwaitingInvestigationAnalysis.vue';
import AwaitingPlayerActionChoiceScreen from '@/components/screens/game-screens/AwaitingPlayerActionChoiceScreen.vue';
import IdleScreen from '@/components/screens/game-screens/IdleScreen.vue';
import type { InProgressGameState } from '@/game/state.ts';
import ScoreBar from '@/components/ui/ScoreBar.vue';
import ChaosBar from '@/components/ui/ChaosBar.vue';
import CardDeck from '@/components/ui/CardDeck.vue';

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