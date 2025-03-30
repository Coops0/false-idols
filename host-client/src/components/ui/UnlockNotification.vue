<template>
  <div class="fixed bottom-0 right-0 p-4 z-50">
    <Transition name="toast" mode="out-in">
      <div v-if="show" class="p-4 bg-gray-100 border-l-4 border-gray-400 shadow-lg rounded" :class="color">
        <slot/>
      </div>
    </Transition>
  </div>
</template>

<script setup lang="ts">
defineProps<{ show: boolean; color: string; }>();
</script>