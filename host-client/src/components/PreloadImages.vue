<template>
  <div class="opacity-0 fixed -z-10">
    <template v-for="icon in ICONS" :key="icon">
      <img :src="PlayerIcon.demon(icon)">
      <img :src="PlayerIcon.normal(icon)">
      <img :src="PlayerIcon.angel(icon)">
      <img :src="PlayerIcon.satan(icon)">
      <img :src="PlayerIcon.dead(icon)">
    </template>

    <img :src="NegativeCard">
    <img :src="PositiveCard">
    <img :src="BoardAngel">
    <img :src="BoardDemon56">
    <img :src="BoardDemon78">
    <img :src="BoardDemon910">
    <img :src="BoardTracker">
  </div>
</template>

<script setup lang="ts">
import NegativeCard from '@/assets/cards/negative-card.png';
import PositiveCard from '@/assets/cards/positive-card.png';
import BoardAngel from '@/assets/board/board-angel.png';
import BoardDemon56 from '@/assets/board/board-demon-5-6.png';
import BoardDemon78 from '@/assets/board/board-demon-7-8.png';
import BoardDemon910 from '@/assets/board/board-demon-9-10.png';
import BoardTracker from '@/assets/board/board-tracker.png';

import { ICONS, PlayerIcon } from '@/game/player-icon.ts';
</script>