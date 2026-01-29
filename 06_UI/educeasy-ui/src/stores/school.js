import { defineStore } from "pinia";
import { api } from "@/services/api";

export const useSchoolStore = defineStore("school", {
  state: () => ({
    schools: [],
    classrooms: [],
    selectedEcoleId: null,
    activeClassId: null,
    loading: {
        schools: false,
        classrooms: false,
        createSchool: false,
        createClassroom: false,
        setMaitre: false,
    },

    error: {
        schools: "",
        classrooms: "",
        setMaitre: "",
    },
  }),

    actions: {
        setSelectedEcole(schoolId) {
            this.selectedEcoleId = schoolId;
        },

        setActiveClass(classId) {
            this.activeClassId = classId;
        },

        async fetchMine({ autoSelectFirst = false } = {}) {
            if (this.loading.schools) return;
            this.loading.schools = true;
            this.error.schools = "";
            try {
                const { data } = await api.get("/schools/mine", { params: { _ts: Date.now() } });
                this.schools = Array.isArray(data) ? data : [];

                if (autoSelectFirst && !this.selectedEcoleId && this.schools.length) {
                    this.selectedEcoleId = this.schools[0].id;
                    await this.fetchClassesForSelectedSchool();
                }

                return this.schools;
            } catch (e) {
                this.error.schools = "Impossible de charger les écoles.";
                throw e;
            } finally {
                this.loading.schools = false;
            }
        },

        async fetchClassesForSelectedSchool() {
            if (!this.selectedEcoleId) {
                this.classrooms = [];
                return [];
            }
            if (this.loading.classrooms) return;
            this.loading.classrooms = true;
            this.error.classrooms = "";
            try {
                const { data } = await api.get(`/classrooms/${this.selectedEcoleId}/list`, { params: { _ts: Date.now() } });
                this.classrooms = Array.isArray(data) ? data : [];
                return this.classrooms;
            } catch (e) {
                this.error.classrooms = "Impossible de charger les classes.";
                throw e;
            } finally {
                this.loading.classrooms = false;
            }
        },

        async createSchool(schoolData) {
            this.loading.createSchool = true;
            try {
                const { data } = await api.post("/schools", schoolData);

                await this.fetchMine({ autoSelectFirst: false });

                if (data?.id) {
                    this.selectedEcoleId = data.id;
                    await this.fetchClassesForSelectedSchool();
                }

                return data;
            } finally {
            this.loading.createSchool = false;
            }
        },

    async createClassroom(classroomData) {
        if (!this.selectedEcoleId) throw new Error("Aucune école sélectionnée.");
        if (this.loading.createClassroom) return;
        this.loading.createClassroom = true;
        try {
            const { data } = await api.post(`/schools/${this.selectedEcoleId}/classrooms`, classroomData);

            await this.fetchClassesForSelectedSchool();

            return data;
        } finally {
            this.loading.createClassroom = false;
        }
        },

    async setMaitre(classroomId, email) {
        const id = classroomId || this.activeClassId;
        if (!id) throw new Error("Aucune classe spécifiée.");
        if (!email || !String(email).trim()) throw new Error("Email manquant.");

        this.loading.setMaitre = true;
        this.error.setMaitre = "";
        try {
            const { data } = await api.post(`/classrooms/${id}/maitre`, email);

            await this.fetchClassesForSelectedSchool();
            return true;
        } catch (e) {
            this.error.setMaitre = "Impossible d'assigner le maître.";
            throw e;
        } finally {
            this.loading.setMaitre = false;
        }
    },

    async fetchMyClassrooms() {
        if (this.loading.classrooms) return;
        this.loading.classrooms = true;
        this.error.classrooms = "";
        try {
            const { data } = await api.get("/classrooms/mine", { params: { _ts: Date.now() } });
            this.classrooms = Array.isArray(data) ? data : [];
            return this.classrooms;
        } catch (e) {
            console.warn("API /classrooms/mine non disponible:", e);
            this.classrooms = [];
            this.error.classrooms = "";
            return [];
        } finally {
            this.loading.classrooms = false;
        }
    },
}
});