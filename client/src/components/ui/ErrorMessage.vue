<template>
  <div
      v-if="localMessage.length"
      class="mb-8 w-full py-4 bg-red-50 border border-red-200 rounded-lg shadow-lg p-4 max-w-md flex items-center justify-between gap-x-4"
      @click="dismiss"
  >
    <p class="text-red-700 text-base">{{ localMessage }}</p>
  </div>
</template>

<script lang="ts" setup>
import { ref, watch } from 'vue';

const props = defineProps<{ message: string }>();
const localMessage = ref(props.message);

watch(() => props.message, m => (localMessage.value = m));

function dismiss() {
  localMessage.value = '';
}
</script>