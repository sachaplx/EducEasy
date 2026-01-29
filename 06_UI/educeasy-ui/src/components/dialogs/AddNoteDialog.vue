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
            v-model="displayDate"
            label="Date (jj/mm/aaaa)"
            placeholder="jj/mm/aaaa"
            :rules="[r.required, r.validDate]"
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
import { ref, watch, computed } from "vue";
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
const formData = ref({
  matiere: "",
  note: null,
  dateNote: new Date().toISOString().slice(0, 10),
  commentaire: "",
});

const displayDate = computed({
  get() {
    if (!formData.value.dateNote) return "";
    const [y, m, d] = formData.value.dateNote.split("-");
    return `${d}/${m}/${y}`;
  },
  set(val) {
    if (!val) {
      formData.value.dateNote = "";
      return;
    }
    const parts = val.split("/");
    if (parts.length === 3) {
      const [d, m, y] = parts;
      formData.value.dateNote = `${y}-${m.padStart(2, "0")}-${d.padStart(2, "0")}`;
    }
  },
});

const r = {
  required: (v) =>
    (v !== null && v !== undefined && String(v).trim().length) || "Requis",
  between: (min, max) => (v) =>
    (v >= min && v <= max) || `Entre ${min} et ${max}`,
  validDate: (v) => {
    if (!v) return true;
    const regex = /^(\d{1,2})\/(\d{1,2})\/(\d{4})$/;
    const match = v.match(regex);
    if (!match) return "Format: jj/mm/aaaa";
    const [, d, m, y] = match;
    const date = new Date(y, m - 1, d);
    if (
      date.getDate() != d ||
      date.getMonth() != m - 1 ||
      date.getFullYear() != y
    ) {
      return "Date invalide";
    }
    return true;
  },
};

function close() {
  open.value = false;
}

function resetForm() {
  formData.value = {
    matiere: "",
    note: null,
    dateNote: new Date().toISOString().slice(0, 10),
    commentaire: "",
  };
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
    resetForm();
    close();
  } catch (error) {
    console.error("Error adding note:", error);
  } finally {
    loading.value = false;
  }
}
</script>
