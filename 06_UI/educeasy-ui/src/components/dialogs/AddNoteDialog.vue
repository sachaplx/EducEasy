<template>
  <v-dialog v-model="open" max-width="520">
    <v-card>
      <v-card-title>Ajouter une note</v-card-title>
      <v-card-text>
        <v-form ref="form" v-model="valid" validate-on="submit">
          <v-text-field
            v-model="formData.matiere"
            label="Matière"
            :rules="[r.required]"
            autocomplete="off"
          />
          <v-text-field
            v-model.number="formData.note"
            label="Note (/20)"
            type="number"
            step="0.25"
            :rules="[r.required, r.between(0, 20)]"
          />
          <v-text-field
            v-model="formData.dateNote"
            label="Date"
            type="date"
            :rules="[r.required]"
          />
          <v-textarea
            v-model="formData.commentaire"
            label="Commentaire (optionnel)"
            rows="3"
            auto-grow
          />
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
  (v) => (open.value = v)
);
watch(open, (v) => emit("update:modelValue", v));

const loading = ref(false);
const valid = ref(false);
const form = ref(null);
const formData = ref({
  matiere: "",
  note: null,
  dateNote: new Date().toISOString().slice(0, 10),
  commentaire: "",
});

const r = {
  required: (v) =>
    (v !== null && v !== undefined && String(v).trim().length) || "Requis",
  between: (min, max) => (v) =>
    (v >= min && v <= max) || `Entre ${min} et ${max}`,
};

function close() {
  open.value = false;
}

async function submit() {
  const ok = await form.value?.validate();
  if (!ok?.valid) return;
  loading.value = true;

  let n = formData.value.note;
  if (typeof n === "string") {
    n = parseFloat(n.replace(",", "."));
  }

  if (Number.isNaN(n) || n < 0 || n > 20) {
    console.error("Invalid note value");
    return;
  }

  const payload = {
    ...formData.value,
    note: n,
  };
  try {
    await api.post(`/pupils/${props.pupilId}/grades/add`, payload);
    emit("saved");
    close();
  } finally {
    loading.value = false;
  }
}
</script>
