<template>
  <div>
    <ErrorToast :message="error"/>
    <LoginScreen
        v-if="canShowLogin && !ws.isConnected"
        v-model="name"
        :is-rejoin="ws.hasEverBeenConnectedSuccessfully"
        @join="() => tryToConnect()"
    />
    <IdleScreen
        v-else-if="game === null || game.state.type === 'idle'"
    />
    <RoleConfirmationScreen
        v-else-if="game.state.type === 'view_role'"
        :game="game"
        @confirm="confirmRole"
    />
    <CommitActionScreen
        v-else-if="game.state.type === 'commit_action'"
        :game="game"
        @commit="commitAction"/>
    <ViewInvestigationResultsScreen
        v-else-if="game.state.type === 'view_investigation_results'"
        :game="game"
        @emit="confirmInvestigation"
    />
    <ChiefDiscardCardScreen
        v-else-if="game.state.type === 'chief_discard_card'"
        :game="game"
        @discard="discardCard"
    />
    <AdvisorChooseCardScreen
        v-else-if="game.state.type === 'advisor_choose_card'"
        :game="game"
        @choose="chooseCard"
    />
    <IdleScreen v-else/>
  </div>
</template>

<script lang="ts" setup>
import { WebsocketOwner } from '@/game/websocket-owner.ts';
import { ActionChoice, type InboundMessage } from '@/game/messages.ts';
import { PlayerIcon } from '@/game/player-icon.ts';
import { Game, type ViewRoleGameState } from '@/game';
import { ref } from 'vue';
import { isNameValid } from '@/util';
import LoginScreen from '@/components/screen/LoginScreen.vue';
import ErrorToast from '@/components/ui/ErrorToast.vue';
import IdleScreen from '@/components/screen/IdleScreen.vue';
import RoleConfirmationScreen from '@/components/screen/RoleConfirmationScreen.vue';
import CommitActionScreen from '@/components/screen/CommitActionScreen.vue';
import ViewInvestigationResultsScreen from '@/components/screen/ViewInvestigationResultsScreen.vue';
import ChiefDiscardCardScreen from '@/components/screen/ChiefDiscardCardScreen.vue';
import AdvisorChooseCardScreen from '@/components/screen/AdvisorChooseCardScreen.vue';

const name = ref<string>('');
const error = ref<string>('');
const playerIcon = ref<PlayerIcon | null>(null);

const ws = new WebsocketOwner(name, handleMessage);
const game = ref<Game | null>(null);

const canShowLogin = ref<boolean>(false);

(async function () {
  name.value = localStorage.getItem('name') ?? '';

  if (isNameValid(name.value)) {
    await tryToConnect();
  } else {
    canShowLogin.value = true;
  }
})();

async function tryToConnect() {
  localStorage.setItem('name', name.value);

  try {
    await ws.connect();
    setTimeout(() => PlayerIcon.preload());
  } catch (err) {
    console.error(err);
    // @ts-ignore
    error.value = err.message;
  } finally {
    // Either way, we have finished the first initial connection attempt,
    // so we can show them the login now.
    canShowLogin.value = true;
  }
}

function handleMessage(message: InboundMessage) {
  console.log(message);
  switch (message.type) {
    case 'assign_icon':
      playerIcon.value = message.icon;
      break;
    case 'assign_role':
      game.value = new Game(message);
      break;
    case 'request_action':
      game.value!.state = {
        type: 'commit_action',
        permittedActions: message.permitted_actions,
        supplementedPlayers: message.players
      };
      break;
    case 'request_chief_card_discard':
      game.value!.state = { type: 'chief_discard_card', cards: message.cards };
      break;
    case 'request_advisor_card_choice':
      game.value!.state = { type: 'advisor_choose_card', cards: message.cards };
      break;
    case 'investigation_result':
      game.value!.state = { type: 'view_investigation_results', player: message.target, role: message.simple_role };
      break;
    case 'disconnect':
      game.value = null;
      playerIcon.value = null;
      ws.disconnect();
      break;
  }
}

function confirmRole() {
  const state = game.value!.state as ViewRoleGameState;
  if (state.hasConfirmed) {
    game.value!.state = { type: 'idle' };
  } else {
    state.hasConfirmed = true;
  }
}

function commitAction(playerName: string, action: ActionChoice) {
  ws.send({ type: 'choose_action', target: playerName, action });
  game.value!.state = { type: 'idle' };
}

function confirmInvestigation() {
  game.value!.state = { type: 'idle' };
}

function discardCard(cardId: number) {
  ws.send({ type: 'discard_one_card', card_id: cardId });
  game.value!.state = { type: 'idle' };
}

function chooseCard(cardId: number) {
  ws.send({ type: 'choose_card', card_id: cardId });
  game.value!.state = { type: 'idle' };
}
</script>
