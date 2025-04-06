<template>
  <div class="h-full flex flex-col p-2 gap-1.5">
    <div class="w-full flex flex-row items-center justify-center gap-x-15">
      <PlayerPreview v-for="player in game.players" :key="player.name" :player size="md"/>
    </div>

    <div class="flex flex-auto size-full items-center justify-center">
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

    <div class="flex h-auto items-center justify-center">
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
import PlayerPreview from '@/components/ui/PlayerPreview.vue';
import AwaitingAdvisorElectionOutcomeScreen
  from '@/components/screens/game-screens/AwaitingAdvisorElectionOutcomeScreen.vue';
import AwaitingPresidentElectionOutcomeScreen
  from '@/components/screens/game-screens/AwaitingPresidentElectionOutcomeScreen.vue';
import AngelBoard from '@/components/ui/AngelBoard.vue';
import DemonBoard from '@/components/ui/DemonBoard.vue';

defineProps<{ game: InProgressGameState }>();
</script>