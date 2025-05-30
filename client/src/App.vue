<template>
  <div class="h-[calc(100vh-64px)] w-full overflow-hidden">
    <PreloadImages
        v-if="playerIcon"
        :player-icon
    />

    <div class="size-full flex flex-col gap-y-4 justify-center items-center p-4">
      <ErrorMessage :message="error"/>
      <Transition mode="out-in" name="fade">
        <LoginScreen
            v-if="canShowLogin && !manualIsConnected"
            v-model="playerName"
            :is-rejoin="ws.hasEverBeenConnectedSuccessfully"
            @join="() => tryToConnect()"
        />
        <IdleScreen
            v-else-if="game === null || game.state.type === 'idle'"
            :connected="manualIsConnected"
            :player-icon
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
        <PresidentDiscardCardScreen
            v-else-if="game.state.type === 'president_discard_card'"
            :game
            @discard="discardCard"
        />
        <AdvisorChooseCardScreen
            v-else-if="game.state.type === 'advisor_choose_card'"
            :game
            @choose="chooseCard"
        />
        <PolicyPeekScreen
            v-else-if="game.state.type === 'policy_peek'"
            :game
            @confirm="confirmPolicyPeek"
        />
        <IdleScreen v-else :connected="manualIsConnected" :player-icon/>
      </Transition>
    </div>

    <Transition mode="out-in" name="fade">
      <div v-if="showRolePopup && lastShownRoleState"
           class="fixed inset-0 bg-black/50 z-50 flex items-center justify-center">
        <RoleConfirmationScreen
            :game="lastShownRoleState"
            :player-icon
            :player-name
            @confirm="confirmRole"
        />
      </div>
    </Transition>
  </div>
</template>

<script lang="ts" setup>
import { WebsocketOwner } from '@/game/websocket-owner.ts';
import { type InboundMessage } from '@/game/messages.ts';
import { Game, type ViewInvestigationResultsGameState, type ViewRoleGameState } from '@/game';
import { onMounted, ref } from 'vue';
import { isNameValid } from '@/util';
import LoginScreen from '@/components/screen/LoginScreen.vue';
import ErrorMessage from '@/components/ui/ErrorMessage.vue';
import IdleScreen from '@/components/screen/IdleScreen.vue';
import RoleConfirmationScreen from '@/components/screen/RoleConfirmationScreen.vue';
import CommitActionScreen from '@/components/screen/CommitActionScreen.vue';
import ViewInvestigationResultsScreen from '@/components/screen/ViewInvestigationResultsScreen.vue';
import PresidentDiscardCardScreen from '@/components/screen/PresidentDiscardCardScreen.vue';
import AdvisorChooseCardScreen from '@/components/screen/AdvisorChooseCardScreen.vue';
import PolicyPeekScreen from '@/components/screen/PolicyPeekScreen.vue';
import type { IconType } from '@/game/player-icon.ts';
import PreloadImages from '@/components/PreloadImages.vue';
import { registerHoldListener } from '@/util/hold-listener.ts';

const playerName = ref<string>('');
const playerIcon = ref<IconType | null>(null);
const error = ref<string>('');
// Weirdness with websockets and get function properties not triggering a rerender on connection
const manualIsConnected = ref(false);
const ws = new WebsocketOwner(playerName, handleMessage, shouldRequestIcon, manualIsConnected);
const game = ref<Game | null>(null);

const canShowLogin = ref<boolean>(false);

const lastShownRoleState = ref<Game | null>(null);
const showRolePopup = ref(false);

onMounted(() => {
  (async function () {
    playerName.value = localStorage.getItem('name') ?? '';

    if (isNameValid(playerName.value)) {
      await tryToConnect();
    } else {
      canShowLogin.value = true;
    }
  })();

  // registerHoldListener(showRole);
});

async function tryToConnect() {
  localStorage.setItem('name', playerName.value);

  try {
    await ws.connect();
  } catch (err) {
    console.error(err);
    // @ts-ignore
    error.value = err.message ?? 'Failed to connect';
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
      return;
    case 'assign_role':
      game.value = new Game(message);
      return;
    case 'disconnect':
      game.value = null;
      playerIcon.value = null;
      ws.disconnect();
      return;
  }

  if (game.value === null) {
    console.warn('Received message when game is null', message);
    game.value = new Game(null);
  }

  switch (message.type) {
    case 'request_action':
      game.value.state = {
        type: 'commit_action',
        action: message.action,
        supplementedPlayers: message.players
      };
      break;
    case 'request_president_card_discard':
      game.value.state = { type: 'president_discard_card', cards: message.cards };
      break;
    case 'request_advisor_card_choice':
      game.value.state = { type: 'advisor_choose_card', cards: message.cards, vetoable: message.vetoable };
      break;
    case 'investigation_result':
      game.value.state = {
        type: 'view_investigation_results',
        player: message.target,
        role: message.simple_role,
        hasConfirmed: false
      };
      break;
    case 'policy_peek':
      game.value.state = {
        type: 'policy_peek',
        cards: message.cards,
      };
  }
}

function shouldRequestIcon(): boolean {
  return playerIcon.value === null;
}

function confirmRole() {
  const state = game.value!.state as ViewRoleGameState;
  if (state.hasConfirmed) {
    game.value!.state = { type: 'idle' };
  } else {
    ws.send({ type: 'confirm' });
    state.hasConfirmed = true;
  }
}

function commitAction(playerName: string) {
  ws.send({ type: 'choose_action', target: playerName });
  game.value!.state = { type: 'idle' };
}

function confirmInvestigation() {
  lastShownRoleState.value = structuredClone(game.value);

  const state = game.value!.state as ViewInvestigationResultsGameState;
  if (state.hasConfirmed) {
    showRolePopup.value = false;
    game.value!.state = { type: 'idle' };
  } else {
    ws.send({ type: 'confirm' });
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

function confirmPolicyPeek() {
  ws.send({ type: 'confirm' });
  game.value!.state = { type: 'idle' };
}

function showRole() {
  if (game.value === null || lastShownRoleState.value === null) return;

  (lastShownRoleState.value.state as ViewRoleGameState).hasConfirmed = true;
  showRolePopup.value = true;
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
  background-color: #f9fafb;
}

* {
  user-select: none;
}

button, input, select, textarea {
  min-height: 44px;
  min-width: 44px;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
