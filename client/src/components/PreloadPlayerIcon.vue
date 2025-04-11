<template>
  <img :src="PlayerIcon.demon(icon)">
  <img :src="PlayerIcon.normal(icon)">
  <img :src="PlayerIcon.angel(icon)">
  <img :src="PlayerIcon.satan(icon)">
</template>

<script setup lang="ts">
import { type IconType, PlayerIcon } from '@/game/player-icon.ts';

defineProps<{ icon: IconType }>();
</script>