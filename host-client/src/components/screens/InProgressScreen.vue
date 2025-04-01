<template>
  <div class="h-screen flex flex-col p-2 gap-2 overflow-hidden">
    <div class="flex-none">
      <div class="grid grid-cols-3 sm:grid-cols-4 md:grid-cols-5 lg:grid-cols-6 xl:grid-cols-9 gap-2">
        <div v-for="player in game.players" :key="player.name"
             class="bg-white/90 rounded-lg border border-gray-200 p-2 flex items-center gap-2">
          <div class="w-12 h-12 rounded-lg overflow-hidden border-2 border-gray-200 flex-shrink-0">
            <img :alt="player.name" :src="PlayerIcon.normal(player.icon)" class="w-full h-full object-cover"/>
          </div>
          <div class="flex-1 min-w-0">
            <div class="font-medium text-sm truncate">{{ player.name }}</div>
            <div class="flex gap-1 mt-0.5">
              <span v-if="player.is_chief" class="inline-flex items-center px-1 py-0.5 rounded-full bg-blue-100 text-xs text-blue-800">
                👑
              </span>
              <span v-if="!player.is_alive" class="inline-flex items-center px-1 py-0.5 rounded-full bg-red-100 text-xs text-red-800">
                💀
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="flex-auto">
      <div class="h-full bg-white/90 rounded-lg border border-gray-200 p-4 overflow-hidden">
        <div class="h-full flex items-center justify-center">
          <AwaitingAdvisorCardChoiceScreen v-if="game.inner_game_state.type === 'awaiting_advisor_card_choice'"
                                           :game="game"/>
          <AwaitingChiefCardDiscardScreen v-else-if="game.inner_game_state.type === 'awaiting_chief_card_discard'"
                                          :game="game"/>
          <AwaitingElectionOutcomeScreen v-else-if="game.inner_game_state.type === 'awaiting_election_outcome'"
                                         :game="game"/>
          <AwaitingInvestigationAnalysis v-else-if="game.inner_game_state.type === 'awaiting_investigation_analysis'"
                                         :game="game"/>
          <AwaitingPlayerActionChoiceScreen v-else-if="game.inner_game_state.type === 'awaiting_chief_action_choice'"
                                            :game="game"/>
          <IdleScreen v-else-if="game.inner_game_state.type === 'idle'" :game="game"/>
        </div>
      </div>
    </div>

    <div class="flex-none">
      <div class="grid grid-cols-3 gap-2">
        <div class="bg-white/90 rounded-lg border border-gray-200 p-2 flex items-center justify-center">
          <CardDeck :game="game" class="w-full h-full"/>
        </div>

        <div class="bg-white/90 rounded-lg border border-gray-200 p-2 flex items-center justify-center">
          <ScoreBar :game="game" class="w-full h-full"/>
        </div>

        <div v-if="game.failed_elections > 0"
             class="bg-white/90 rounded-lg border border-gray-200 p-2 flex items-center justify-center">
          <ChaosBar :game="game" class="w-full h-full"/>
        </div>

        <div v-else class="bg-white/90 rounded-lg border border-gray-200 p-2 flex items-center justify-center">
          <div class="text-gray-400 text-sm">No failed elections</div>
        </div>
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
import { computed } from 'vue';
import { PlayerIcon } from '@/game/player-icon.ts';
import ChaosBar from '@/components/ui/ChaosBar.vue';
import ScoreBar from '@/components/ui/ScoreBar.vue';
import CardDeck from '@/components/ui/CardDeck.vue';

defineProps<{ game: InProgressGameState }>();
</script>