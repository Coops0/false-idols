<template>
  <div>
    <ErrorToast :message="error"/>
    <LoginScreen
        v-if="canShowLogin && !ws.isConnected"
        v-model="name"
        :is-rejoin="ws.hasEverBeenConnectedSuccessfully"
        @join="() => tryToConnect()"
    />
    <IdleScreen v-else-if="game === null || game.state.type === 'idle'"/>
    <RoleConfirmationScreen v-else-if="" :game="game!" @confirm="confirmRole"/>
  </div>
</template>

<script setup lang="ts">
import { WebsocketOwner } from '@/game/websocket-owner.ts';
import { type InboundMessage, Role } from '@/game/messages.ts';
import { PlayerIcon } from '@/game/player-icon.ts';
import { Game } from '@/game';
import { ref } from 'vue';
import { isNameValid } from '@/util';
import LoginScreen from '@/components/screen/LoginScreen.vue';
import ErrorToast from '@/components/ui/ErrorToast.vue';
import IdleScreen from '@/components/screen/IdleScreen.vue';
import RoleConfirmationScreen from '@/components/screen/RoleConfirmationScreen.vue';

const name = ref<string>('');
const error = ref<string>('');
const playerIcon = ref<string>('');

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
  switch (message.type) {
    case 'assign_icon':
      console.log('Icon assigned', message.icon);
      playerIcon.value = message.icon;
      break;
    case 'assign_role': {
      console.log('Role assigned', message.role, message.demon_count);
      if (message.role === Role.DEMON) {
        console.log('Teammates', message.teammates);
        console.log('Satan', message.satan);
      }

      game.value = new Game(message);
      break;
    }
    case 'request_action':
      console.log('Request action', message.permitted_actions, message.players);
      break;
    case 'request_chief_card_discard':
      console.log('Discard one card', message.cards);

      break;
    case 'request_advisor_card_choice':
      console.log('Choose card', message.cards);
      break;
    case 'investigation_result':
      console.log('Investigation result', message.target, message.simple_role);
      break;
    case 'disconnect': {
      console.log('Disconnecting', message.reason);
      ws.disconnect();
      break;
    }
    default: {
      console.warn('Unhandled message', message);
    }
  }
}

function confirmRole() {

}
</script>
