<template>
  <div
      v-if="localMessage.length"
      class="fixed bottom-4 left-auto right-4 w-auto bg-red-50 border border-red-200 rounded-lg shadow-lg p-4 max-w-md flex items-center justify-between space-x-4"
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