<template>
  <div class="fixed bottom-0 right-0 p-4 z-50">
    <Transition mode="out-in" name="toast">
      <div v-if="show" :class="color" class="p-4 bg-gray-100 border-l-4 border-gray-400 shadow-lg rounded">
        <slot/>
      </div>
    </Transition>
  </div>
</template>

<script lang="ts" setup>
defineProps<{ show: boolean; color: string; }>();
</script>