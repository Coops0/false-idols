<template>
  <div class="size-full flex flex-col items-center justify-center">
    <div class="space-y-6">
      <input
          v-model="name"
          class="w-full px-4 py-3 rounded-lg border border-gray-300 focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition-colors text-base"
          placeholder="Name"
          type="text"
          @keydown.enter="join"
      />

      <BaseButton
          :disabled="!isNameValid(name)"
          class="w-full"
          variant="primary"
          @click="join"
      >
        {{ isRejoin ? 'Rejoin' : 'Join' }}
      </BaseButton>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { isNameValid } from '@/util';
import BaseButton from '@/components/ui/BaseButton.vue';

defineProps<{ isRejoin: boolean; }>();
const name = defineModel<string>({ required: true });
const emit = defineEmits<{ join: [] }>();

function join() {
  if (isNameValid(name.value)) {
    emit('join');
  }
}
</script>