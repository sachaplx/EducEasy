<template>
  <v-dialog v-model="open" max-width="560">
    <v-card>
      <v-card-title>Ajouter une remarque</v-card-title>
      <v-card-text>
        <v-form ref="form" v-model="valid" validate-on="submit">
          <v-select
            v-model="formData.type"
            :items="levels"
            label="Type"
            :rules="[r.required]"
          />
          <v-textarea
            v-model="formData.contenu"
            label="Contenu"
            :rules="[r.required, r.min(4)]"
            rows="5"
            auto-grow
          />
          <div class="text-caption text-medium-emphasis mt-1">
            L’auteur sera défini automatiquement à partir de votre compte (prof
            connecté).
          </div>
        </v-form>
      </v-card-text>
      <v-card-actions class="justify-end">
        <v-btn variant="text" @click="close">Annuler</v-btn>
        <v-btn color="primary" :loading="loading" @click="submit"
          >Enregistrer</v-btn
        >
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup>
import { ref, watch } from "vue";
import { api } from "@/services/api";

const props = defineProps({
  modelValue: { type: Boolean, default: false },
  pupilId: { type: Number, required: true },
});
const emit = defineEmits(["update:modelValue", "saved"]);

const open = ref(props.modelValue);
watch(
  () => props.modelValue,
  (v) => (open.value = v),
);
watch(open, (v) => emit("update:modelValue", v));

const loading = ref(false);
const valid = ref(false);
const form = ref(null);

const levels = [
  { title: "Info", value: "INFO" },
  { title: "Avertissement", value: "WARNING" },
  { title: "Important", value: "CRITICAL" },
];

const formData = ref({
  type: "INFO",
  contenu: "",
});

const r = {
  required: (v) =>
    (v !== null && v !== undefined && String(v).trim().length) || "Requis",
  min: (n) => (v) =>
    String(v || "").trim().length >= n || `Min. ${n} caractères`,
};

function close() {
  open.value = false;
}

function resetForm() {
  formData.value = {
    type: "INFO",
    contenu: "",
  };
  form.value?.reset();
}

async function submit() {
  const ok = await form.value?.validate();
  if (!ok?.valid) return;
  loading.value = true;
  try {
    await api.post(`/pupils/${props.pupilId}/remarks/add`, formData.value);
    emit("saved");
    resetForm();
    close();
  } catch (error) {
    console.error("Error adding remark:", error);
  } finally {
    loading.value = false;
  }
}
</script>
