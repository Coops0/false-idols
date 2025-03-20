<template>
  <div v-if="localMessage.length" class="w-12 h-4 text-lg text-red-500">
    {{ localMessage }}
    <button @click="dismiss">X</button>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue';

const props = defineProps<{ message: string }>();
const localMessage = ref(props.message);

watch(() => props.message, m => {
  localMessage.value = m;
});

function dismiss() {
  localMessage.value = '';
}
</script>