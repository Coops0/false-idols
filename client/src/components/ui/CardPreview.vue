<template>
  <div>
    {{ card.description }}
    {{ card.consequence }}
  </div>
</template>

<script lang="ts" setup>
import type { Card } from '@/game/messages.ts';

defineProps<{ card: Card }>();
</script>