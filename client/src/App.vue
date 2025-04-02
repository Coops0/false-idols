<template>
  <div class="min-h-screen w-full overflow-x-hidden">
    <ErrorToast :message="error"/>
    <LoginScreen
        v-if="canShowLogin && !manualIsConnected"
        v-model="playerName"
        :is-rejoin="ws.hasEverBeenConnectedSuccessfully"
        @join="() => tryToConnect()"
    />
    <IdleScreen
        v-else-if="game === null || game.state.type === 'idle'"
    />
    <RoleConfirmationScreen
        v-else-if="game.state.type === 'view_role'"
        :game
        :player-icon
        :player-name
        @confirm="confirmRole"
    />
    <CommitActionScreen
        v-else-if="game.state.type === 'commit_action'"
        :game
        @commit="commitAction"
    />
    <ViewInvestigationResultsScreen
        v-else-if="game.state.type === 'view_investigation_results'"
        :game
        @confirm="confirmInvestigation"
    />
    <ChiefDiscardCardScreen
        v-else-if="game.state.type === 'chief_discard_card'"
        :game
        @discard="discardCard"
    />
    <AdvisorChooseCardScreen
        v-else-if="game.state.type === 'advisor_choose_card'"
        :game
        @choose="chooseCard"
    />
    <IdleScreen v-else/>
  </div>
</template>

<script lang="ts" setup>
import { WebsocketOwner } from '@/game/websocket-owner.ts';
import { ActionChoice, type InboundMessage, Role } from '@/game/messages.ts';
import { PlayerIcon } from '@/game/player-icon.ts';
import { Game, type ViewInvestigationResultsGameState, type ViewRoleGameState } from '@/game';
import { onMounted, ref } from 'vue';
import { isNameValid } from '@/util';
import LoginScreen from '@/components/screen/LoginScreen.vue';
import ErrorToast from '@/components/ui/ErrorToast.vue';
import IdleScreen from '@/components/screen/IdleScreen.vue';
import RoleConfirmationScreen from '@/components/screen/RoleConfirmationScreen.vue';
import CommitActionScreen from '@/components/screen/CommitActionScreen.vue';
import ViewInvestigationResultsScreen from '@/components/screen/ViewInvestigationResultsScreen.vue';
import ChiefDiscardCardScreen from '@/components/screen/ChiefDiscardCardScreen.vue';
import AdvisorChooseCardScreen from '@/components/screen/AdvisorChooseCardScreen.vue';

const playerName = ref<string>('');
const playerIcon = ref<string>('');
const error = ref<string>('');
// Weirdness with websockets and get function properties not triggering a rerender on connection
const manualIsConnected = ref(false);

const ws = new WebsocketOwner(playerName, handleMessage, manualIsConnected);
const game = ref<Game | null>(null);

const canShowLogin = ref<boolean>(false);

onMounted(() => {
  (async function () {
    playerName.value = localStorage.getItem('name') ?? '';

    if (isNameValid(playerName.value)) {
      await tryToConnect();
    } else {
      canShowLogin.value = true;
    }
  })();
});

async function tryToConnect() {
  localStorage.setItem('name', playerName.value);

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
    case 'disconnect':
      game.value = null;
      playerIcon.value = '';
      ws.disconnect();
      break;
  }

  if (game.value === null) {
    game.value = new Game({
      type: 'assign_role',
      demon_count: 0,
      role: Role.ANGEL
    });

    game.value.state = { type: 'idle' };
  }

  switch (message.type) {
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
      game.value!.state = {
        type: 'view_investigation_results',
        player: message.target,
        role: message.simple_role,
        hasConfirmed: false
      };
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
  const state = game.value!.state as ViewInvestigationResultsGameState;
  if (state.hasConfirmed) {
    game.value!.state = { type: 'idle' };
  } else {
    state.hasConfirmed = true;
  }
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

<style>
html {
  font-size: 16px;
  -webkit-text-size-adjust: 100%;
}

body {
  margin: 0;
  padding: 0;
  width: 100%;
  overflow-x: hidden;
  -webkit-tap-highlight-color: transparent;
}

button, input, select, textarea {
  min-height: 44px;
  min-width: 44px;
}

@media screen and (max-width: 768px) {
  html {
    font-size: 16px;
  }
}
</style>
