<template>
  <div class="min-h-screen flex items-center justify-center p-4">
    <BaseCard class="w-full max-w-md mx-4">
      <div class="space-y-6">
        <div>
          <input
              v-model="name"
              class="w-full px-4 py-3 rounded-lg border border-gray-300 focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition-colors text-base"
              placeholder="Name"
              type="text"
              @keydown.enter="join"
          />
        </div>

        <BaseButton
            :disabled="!isNameValid(name)"
            class="w-full py-3 text-base font-medium"
            variant="primary"
            @click="join"
        >
          {{ isRejoin ? 'Rejoin' : 'Join' }}
        </BaseButton>
      </div>
    </BaseCard>
  </div>
</template>

<script lang="ts" setup>
import { isNameValid } from '@/util';
import BaseButton from '@/components/ui/BaseButton.vue';
import BaseCard from '@/components/ui/BaseCard.vue';

defineProps<{ isRejoin: boolean; }>();
const name = defineModel<string>({ required: true });
const emit = defineEmits<{ join: [] }>();

function join() {
  if (isNameValid(name.value)) {
    emit('join');
  }
}
</script>