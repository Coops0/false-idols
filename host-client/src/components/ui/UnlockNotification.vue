<template>
  <div class="fixed top-0 right-16 p-4 z-50">
    <Transition mode="out-in" name="toast">
      <div v-if="show" :class="color" class="p-4 bg-gray-100 border-2 shadow-lg rounded-lg text-gray-800">
        <slot/>
      </div>
    </Transition>
  </div>
</template>

<script lang="ts" setup>
defineProps<{ show: boolean; color: string; }>();
</script>