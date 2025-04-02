<template>
  <div
      v-if="localMessage.length"
      class="fixed bottom-4 left-4 right-4 sm:left-auto sm:right-4 sm:w-auto bg-red-50 border border-red-200 rounded-lg shadow-lg p-3 sm:p-4 max-w-md flex items-center justify-between space-x-3 sm:space-x-4"
  >
    <p class="text-red-700 text-sm sm:text-base">{{ localMessage }}</p>
    <button
        aria-label="Dismiss error"
        class="text-red-500 focus:outline-none p-1 -m-1"
        @click="dismiss"
    >
      <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path d="M6 18L18 6M6 6l12 12" stroke-linecap="round" stroke-linejoin="round" stroke-width="2"/>
      </svg>
    </button>
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