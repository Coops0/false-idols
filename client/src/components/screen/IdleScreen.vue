<template>
  <div class="size-full flex flex-col gap-1 items-center justify-center">
    <p class="text-gray-400 text-sm text-center">(connected)</p>
    <PlayerPreview v-if="mockPlayer !== null" :player="mockPlayer" hide-name/>
  </div>
</template>
<script setup lang="ts">
import PlayerPreview from '@/components/ui/PlayerPreview.vue';
import { computed } from 'vue';
import type { Player } from '@/game/messages.ts';
import type { IconType } from '@/game/player-icon.ts';

const props = defineProps<{ playerIcon: IconType | null }>();
const mockPlayer = computed<Player | null>(() => props.playerIcon ? ({ name: '', icon: props.playerIcon }) : null);
</script>