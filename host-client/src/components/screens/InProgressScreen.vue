<template>
  <div class="h-full flex flex-col p-2 gap-2 overflow-hidden">
    <div class="flex-none">
      <div class="grid grid-cols-3 sm:grid-cols-4 md:grid-cols-5 lg:grid-cols-6 xl:grid-cols-9 gap-2">
        <PlayerCard v-for="player in game.players" :key="player.name" :player size="md"/>
      </div>
    </div>

    <div class="flex-auto">
      <div class="h-full bg-white/90 rounded-lg border border-gray-200 p-4 overflow-hidden">
        <div class="h-full flex items-center justify-center">
          <!--@formatter:off-->
          <AwaitingAdvisorCardChoiceScreen v-if="game.inner_game_state.type === 'awaiting_advisor_card_choice'" :game/>
          <AwaitingPresidentCardDiscardScreen v-else-if="game.inner_game_state.type === 'awaiting_president_card_discard'" :game/>
          <AwaitingAdvisorElectionOutcomeScreen v-else-if="game.inner_game_state.type === 'awaiting_advisor_election_outcome'" :game/>
          <AwaitingPresidentElectionOutcomeScreen v-else-if="game.inner_game_state.type === 'awaiting_president_election_outcome'" :game/>
          <AwaitingInvestigationAnalysis v-else-if="game.inner_game_state.type === 'awaiting_investigation_analysis'" :game/>
          <AwaitingPlayerActionChoiceScreen v-else-if="game.inner_game_state.type === 'awaiting_president_action_choice'" :game/>
          <IdleScreen v-else-if="game.inner_game_state.type === 'idle'" :game/>
          <!--@formatter:on-->
        </div>
      </div>
    </div>

    <div class="flex h-full items-center justify-center">
      <div class="relative justify-center items-center flex flex-row gap-2">
        <AngelBoard :cards="game.deck.played_cards" :players-size="game.players.length"
                    :failed-elections="game.failed_elections"/>
        <DemonBoard :cards="game.deck.played_cards" :players-size="game.players.length"/>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import AwaitingAdvisorCardChoiceScreen from '@/components/screens/game-screens/AwaitingAdvisorCardChoiceScreen.vue';
import AwaitingPresidentCardDiscardScreen
  from '@/components/screens/game-screens/AwaitingPresidentCardDiscardScreen.vue';
import AwaitingInvestigationAnalysis from '@/components/screens/game-screens/AwaitingInvestigationAnalysis.vue';
import AwaitingPlayerActionChoiceScreen from '@/components/screens/game-screens/AwaitingPlayerActionChoiceScreen.vue';
import IdleScreen from '@/components/screens/game-screens/IdleScreen.vue';
import type { InProgressGameState } from '@/game/state.ts';
import PlayerCard from '@/components/ui/PlayerCard.vue';
import AwaitingAdvisorElectionOutcomeScreen
  from '@/components/screens/game-screens/AwaitingAdvisorElectionOutcomeScreen.vue';
import AwaitingPresidentElectionOutcomeScreen
  from '@/components/screens/game-screens/AwaitingPresidentElectionOutcomeScreen.vue';
import AngelBoard from '@/components/ui/AngelBoard.vue';
import DemonBoard from '@/components/ui/DemonBoard.vue';

defineProps<{ game: InProgressGameState }>();
</script>