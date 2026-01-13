<template>
  <v-dialog v-model="open" max-width="560">
    <v-card rounded="xl">
      <v-card-title class="d-flex align-center justify-space-between">
        <span class="text-h6 font-weight-bold">Définir le maître</span>
        <v-btn icon variant="text" @click="open = false" aria-label="Fermer">
          <v-icon>mdi-close</v-icon>
        </v-btn>
      </v-card-title>

      <v-card-text>
        <v-form ref="formRef" @submit.prevent="onSubmit">
          <v-text-field
            v-model="form.email"
            label="Email enseignant"
            variant="outlined"
            :rules="[rules.required, rules.email]"
            autocomplete="off"
          />

          <div class="d-flex justify-end mt-4" style="gap: 8px">
            <v-btn variant="text" @click="open = false">Annuler</v-btn>
            <v-btn
              type="submit"
              color="primary"
              :loading="loading"
              :disabled="disabled"
            >
              Enregistrer
            </v-btn>
          </div>
        </v-form>
      </v-card-text>
    </v-card>
  </v-dialog>
</template>

<script setup>
import { reactive, ref, watch, computed } from "vue";

const props = defineProps({
  modelValue: { type: Boolean, default: false },
  loading: { type: Boolean, default: false },
  classroomId: { type: Number, default: null },
});

const emit = defineEmits(["update:modelValue", "submit"]);

const open = ref(props.modelValue);
watch(
  () => props.modelValue,
  (v) => (open.value = v)
);
watch(open, (v) => emit("update:modelValue", v));

const disabled = computed(() => !props.classroomId);

const formRef = ref(null);
const form = reactive({ email: "" });

const rules = {
  required: (v) => (!!v && String(v).trim().length > 0) || "Champ requis",
  email: (v) =>
    !v ||
    /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(String(v).trim()) ||
    "Email invalide",
};

watch(open, (v) => {
  if (!v) return;
  form.email = "";
});

async function onSubmit() {
  if (props.loading || disabled.value) return;
  const res = await formRef.value?.validate?.();
  if (res && res.valid === false) return;

  emit("submit", { classroomId: props.classroomId, email: form.email });
}
</script>
