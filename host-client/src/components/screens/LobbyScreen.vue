<template>
  <div class="h-full flex flex-col p-2 gap-2">
    <div class="flex items-center justify-center m-3">
      <div class="text-center">
        <h1 class="text-9xl font-amazonia font-black text-gray-900 mb-4">False Idols</h1>
        <div class="inline-block">
          <QrcodeSvg
              :size="150"
              :value="`${domain}/ok`"
              level="L"
          />
        </div>
      </div>
    </div>

    <div class="flex-grow m-3">
      <div class="w-full flex flex-row gap-x-14 gap-y-7">
        <PlayerPreview
            v-for="player in game.players"
            :key="player.name"
            :player
            size="lg"
            ignore-modifiers
            @click="() => emit('kick', player.name)"
        />
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import type { LobbyGameState } from '@/game/state.ts';
import { computed } from 'vue';
import PlayerPreview from '@/components/ui/PlayerPreview.vue';
import { QrcodeSvg } from 'qrcode.vue';

defineProps<{ game: LobbyGameState }>();
const emit = defineEmits<{ kick: [playerName: string] }>();

const domain = computed(() => {
  if (!window.location) return 'LOADING';
  return `${window.location.protocol}//${window.location.host}`;
});
</script>