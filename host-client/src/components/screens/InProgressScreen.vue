<template>
  <div class="h-full flex flex-col p-2 gap-1.5">
    <div class="w-full flex flex-row items-center justify-center gap-x-15">
      <PlayerPreview
          v-for="player in game.players"
          :key="player.name"
          :player
          :is-president="player.is_president"
          size="md"
      />
    </div>

    <div class="flex flex-auto size-full items-center justify-center">
      <Transition mode="out-in" name="fade">
        <!--@formatter:off-->
        <AwaitingAdvisorCardChoiceScreen v-if="game.inner_game_state.type === 'awaiting_advisor_card_choice'" :game/>
        <AwaitingPresidentCardDiscardScreen v-else-if="game.inner_game_state.type === 'awaiting_president_card_discard'" :game/>
        <AwaitingAdvisorElectionOutcomeScreen v-else-if="game.inner_game_state.type === 'awaiting_advisor_election_outcome'" :game/>
        <AwaitingPresidentElectionOutcomeScreen v-else-if="game.inner_game_state.type === 'awaiting_president_election_outcome'" :game/>
        <AwaitingInvestigationAnalysis v-else-if="game.inner_game_state.type === 'awaiting_investigation_analysis'" :game/>
        <AwaitingPolicyPeekScreen v-else-if="game.inner_game_state.type === 'awaiting_policy_peek'" :game/>
        <AwaitingPlayerActionChoiceScreen v-else-if="game.inner_game_state.type === 'awaiting_president_action_choice'" :game/>
        <AwaitingRoleConfirmationsScreen v-else-if="game.inner_game_state.type === 'awaiting_role_confirmations'" :game/>
        <IdleScreen v-else-if="game.inner_game_state.type === 'idle'" :game/>
        <!--@formatter:on-->
      </Transition>
    </div>

    <div class="flex h-auto items-center justify-center">
      <div class="relative justify-center items-center flex flex-row gap-2">
        <AngelBoard :cards="game.deck.played_cards" :failed-elections="game.failed_elections"
                    :players-size="game.players.length"/>
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
import { CardConsequence, type CardDeck, type InProgressGameState } from '@/game/state.ts';
import PlayerPreview from '@/components/ui/PlayerPreview.vue';
import AwaitingAdvisorElectionOutcomeScreen
  from '@/components/screens/game-screens/AwaitingAdvisorElectionOutcomeScreen.vue';
import AwaitingPresidentElectionOutcomeScreen
  from '@/components/screens/game-screens/AwaitingPresidentElectionOutcomeScreen.vue';
import AngelBoard from '@/components/ui/AngelBoard.vue';
import DemonBoard from '@/components/ui/DemonBoard.vue';
import { watch } from 'vue';
import { toast } from 'vue3-toastify';
import AwaitingRoleConfirmationsScreen from '@/components/screens/game-screens/AwaitingRoleConfirmationsScreen.vue';
import AwaitingPolicyPeekScreen from '@/components/screens/game-screens/AwaitingPolicyPeekScreen.vue';

const props = defineProps<{ game: InProgressGameState }>();

const negativeCards = (deck: CardDeck) => deck.played_cards.filter(card => card.consequence === CardConsequence.NEGATIVE).length;

const NEGATIVE_CARD_COUNT_SATAN_ELECTION_WIN = 4;
watch(() => props.game, (game, oldGame) => {
  if (!oldGame || !game) return;

  const newNegativeCards = negativeCards(game.deck);
  const oldNegativeCards = negativeCards(oldGame.deck);

  if (newNegativeCards >= NEGATIVE_CARD_COUNT_SATAN_ELECTION_WIN && oldNegativeCards < NEGATIVE_CARD_COUNT_SATAN_ELECTION_WIN) {
    toast('Demons will win if Satan is elected as advisor', {
      position: toast.POSITION.TOP_CENTER,
      type: toast.TYPE.WARNING,
      autoClose: false
    });
  }
});
</script>