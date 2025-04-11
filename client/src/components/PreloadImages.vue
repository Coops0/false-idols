<template>
  <div class="opacity-0 fixed -z-10">
    <PreloadPlayerIcon :icon="playerIcon"/>

    <img :src="NegativeCard">
    <img :src="PositiveCard">
    <img :src="NeutralCard">

    <template v-if="loadAll" v-for="icon in otherIcons" :key="icon">
      <PreloadPlayerIcon :icon/>
    </template>
  </div>
</template>

<script lang="ts" setup>
import NegativeCard from '@/assets/cards/negative-card.png';
import PositiveCard from '@/assets/cards/positive-card.png';
import NeutralCard from '@/assets/cards/neutral-card.png';

import { ICONS, type IconType } from '@/game/player-icon.ts';
import { computed, onMounted, ref } from 'vue';
import PreloadPlayerIcon from '@/components/PreloadPlayerIcon.vue';

const props = defineProps<{ playerIcon: IconType }>();

const otherIcons = computed(() => ICONS.filter(icon => props.playerIcon !== icon));

const loadAll = ref(false);

onMounted(() => {
  setTimeout(() => (loadAll.value = true), 2000);
});
</script>