<template>
  <div class="opacity-0 fixed -z-10">
    <template v-for="icon in ICONS" :key="icon">
      <img :src="PlayerIcon.demon(icon)">
      <img :src="PlayerIcon.normal(icon)">
      <img :src="PlayerIcon.angel(icon)">
      <img :src="PlayerIcon.satan(icon)">
    </template>

    <img :src="NegativeCard">
    <img :src="PositiveCard">
    <img :src="NeutralCard">
  </div>
</template>

<script lang="ts" setup>
import NegativeCard from '@/assets/cards/negative-card.png';
import PositiveCard from '@/assets/cards/positive-card.png';
import NeutralCard from '@/assets/cards/neutral-card.png';

import { ICONS, PlayerIcon } from '@/game/player-icon.ts';
</script>