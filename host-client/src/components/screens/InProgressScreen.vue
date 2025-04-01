<template>
  <div class="h-screen flex flex-col p-4 gap-2">
    <div class="h-[35vh]">
      <div class="grid grid-cols-4 gap-2 h-full">
        <div v-for="player in game.players" :key="player.name"
             class="bg-white/90 rounded-lg border border-gray-200 p-2 flex items-center gap-2">
          <div class="w-12 h-12 rounded-lg overflow-hidden border-2 border-gray-200">
            <img :alt="player.name" :src="PlayerIcon.normal(player.icon)" class="w-full h-full object-cover"/>
          </div>
          <div class="flex-1 min-w-0">
            <div class="font-medium truncate">{{ player.name }}</div>
            <div class="text-sm text-gray-500">{{ player.role }}</div>
          </div>
        </div>
      </div>
    </div>

    <div class="h-[35vh] relative">
      <div class="absolute inset-0 bg-white/90 rounded-lg border border-gray-200 p-4">
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

    <div class="h-[15vh] grid grid-cols-3 gap-2">
      <div class="bg-white/90 rounded-lg border border-gray-200 p-2">
        <CardDeck :game class="h-full"/>
      </div>
      <div class="bg-white/90 rounded-lg border border-gray-200 p-2">
        <ScoreBar :game class="h-full"/>
      </div>
      <div v-if="game.failed_elections > 0" class="bg-white/90 rounded-lg border border-gray-200 p-2">
        <ChaosBar :game class="h-full"/>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
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
import { PlayerIcon } from '@/game/player-icon.ts';

defineProps<{ game: InProgressGameState }>();
</script>

<style scoped>
* {
  transition: none !important;
  animation: none !important;
}
</style>