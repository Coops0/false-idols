<template>
<div>

  <p>You cannot show anyone this screen</p>
  <BaseButton
    class="w-full"
    @click="() => emit('confirm')"
    />
</div>
</template>

<script setup lang="ts">
import { Game, type PolicyPeekGameState } from '@/game';
import { computed } from 'vue';
import BaseButton from '@/components/ui/BaseButton.vue';

const props = defineProps<{ game: Game; }>();
const gameState = computed(() => props.game.state as PolicyPeekGameState);
const emit = defineEmits<{ confirm: [] }>();
</script>