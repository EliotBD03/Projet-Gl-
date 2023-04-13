<template>
    <div class="main">
        <div class="header">
            <MainHeader text="header.notifications"/>
        </div>
        <div class="notifs">
            <MainNotification class="notif" v-for="notif in notifications" :providerId="notif.providerProposalId" :role="role" :key="notif.notificationId" :time="notif.creationDate" :text="notif.context" :id_notification="notif.notificationId" :proposal-name="notif.proposalName" @seeProposal="getContract" @delete="deleteNotifications" @accept="acceptNotification" @refuse="refuseNotification"/>
        </div>
        <div class="bottombuttons">
            <div class="homebutton" @click.prevent.left="redirecting()">
                <GoButton text="header.home" :colore="'#B1B9FC'"/>
            </div>
            <div v-if="notLastPage()" @click.prevent.left="loader()">
                <GoButton text="button.seemore" :colore="'#B1B9FC'"/>
            </div>
            <div class="refresh-button" @click="refreshNotifications">
                <GoButton text="button.refresh" :colore="'#B1B9FC'"/>
            </div>
        </div>
    </div>
</template>

<script>
import MainHeader from "@/components/MainHeader.vue";
import MainNotification from "@/components/MainNotification.vue";
import GoButton from "@/components/GoButton.vue";
import GlobalMethods from "@/components/GlobalMethods.vue";
import Swal from "sweetalert2";
export default {
    components: {
        MainNotification,
        MainHeader,
        GoButton
    },
    data() {
        return {
            notifications: [],
            nbr: 1,
            lastPage: 0,
            loading : false,
            timer: null,
            role: this.$cookies.get('role'),
        }
    },
    created() {
        this.getNotifications();
        GlobalMethods.getCurrentLanguage();
        this.timer = setInterval(() => {
            this.refreshNotifications();
        }, 5 * 60 * 1000);
    },
    beforeDestroy() {
        clearInterval(this.timer);
    },
    /*Méthode pour rediriger vers la page d'accueil*/
    methods: {
        async refreshNotifications(){
            await this.getNotifications();
        },
        notLastPage(){
            if(this.lastPage == this.nbr || this.loading == true){
                return false;
            }
            return true;
        },
        loader()
        {
            if(!this.loading)
            {
                this.nbr++;
                this.getPage();
            }
        },
        async getContract(id, name) {
            if (this.$cookies.get('role') === 'supplier') {
                const requestOptions = {
                    method: 'GET',
                    headers: {'Authorization': this.$cookies.get('token')}
                };
                try {
                    const response = await fetch('https://babawallet.alwaysdata.net/api/provider/proposals/' + name, requestOptions);
                    if (!response.ok) {
                        if (response.status === 401) {
                            throw new Error('Token');
                        } else {
                            const data = await response.json();
                            throw new Error(data.error);
                        }
                    } else {
                        const data = await response.json();
                        this.contract = data.proposal;
                        Swal.fire({
                            icon: 'info',
                            title: this.contract.proposalName,
                            html: `${this.$t("proposal.typeofenergy")}: ${this.contract.typeOfEnergy}<br>
           ${this.$t("proposal.location")}: ${this.convertLocation(this.contract.location)}<br>
           ${this.$t("proposal.priceperday")}: ${this.contract.variableDayPrice}<br>
           ${this.$t("proposal.pricepernight")}: ${this.contract.variableNightPrice}
           ${this.$t("proposal.duration")}: ${this.contract.duration}<br>
            ${this.$t("proposal.rate")}: ${this.convertRate(this.contract.fixedRate)}`
                        });
                    }
                } catch (error) {
                    if (error.message === 'Token') {
                        this.$cookies.remove('token');
                        this.$cookies.remove('role');
                        Swal.fire(this.$t("alerts.connectionexpired"));
                        this.$router.push('/');
                    } else {
                        GlobalMethods.errorApi(error.message);
                    }
                }
            } else {
                try {
                    const requestsOptions = {
                        method: 'GET',
                        headers: {'Authorization': this.$cookies.get('token')}
                    };
                    const response = await fetch("https://babawallet.alwaysdata.net/api/client/proposals/" + id + "/" + name, requestsOptions);
                    if (!response.ok) {
                        if (response.status === 401) {
                            throw new Error("Token");
                        } else {
                            const data = await response.json();
                            throw new Error(data.error);
                        }
                    } else {
                        const data = await response.json();
                        this.contract = data.proposal;
                        Swal.fire({
                            icon: 'info',
                            title: this.contract.proposalName,
                            html: `${this.$t("proposal.typeofenergy")}: ${this.contract.typeOfEnergy}<br>
           ${this.$t("proposal.location")}: ${this.convertLocation(this.contract.location)}<br>
           ${this.$t("proposal.priceperday")}: ${this.contract.variableDayPrice}<br>
           ${this.$t("proposal.pricepernight")}: ${this.contract.variableNightPrice}<br>
            ${this.$t("proposal.duration")}: ${this.contract.duration/720}<br>
            ${this.$t("proposal.rate")}: ${this.convertRate(this.contract.fixedRate)}`
                        });
                    }
                } catch (error) {
                    if (error.message === "Token") {
                        this.$cookies.remove("token");
                        this.$cookies.remove("role");
                        Swal.fire(this.$t("alerts.connectionexpired"));
                        this.$router.push("/");
                    } else {
                        GlobalMethods.errorApi(error.message);
                    }
                }
            }
        },
        convertRate(value) {
            if (value === false) {
                return this.$t('proposal.variable');
            }
            else {
                return this.$t('proposal.fixed');
            }
        },
        async deleteNotifications(id_notification) {
            console.log("test");
            const requestOptions = {
                method: "DELETE",
                headers: {'Authorization': this.$cookies.get("token")},
            }
            fetch('https://babawallet.alwaysdata.net/api/common/notifications/' + id_notification, requestOptions)
                .then(response => {
                    if (!response.ok) {
                        if (response.status == 401) {
                            throw new Error("Token");
                        } else {
                            const data = response.json();
                            throw new Error(data.error);
                        }
                    } else {
                        Swal.fire(this.$t("alerts.deletednotification"));
                        this.refreshNotifications();
                    }
                })
                .catch(error => {
                    if (error.message === "Token") {
                        GlobalMethods.errorToken();
                    } else {
                        GlobalMethods.errorApi(error.message);
                    }
                });
            await this.refreshNotifications();
        },
        async getNotifications() {
            const requestOptions = {
                method: "GET",
                headers: {'Authorization': this.$cookies.get("token")},
            };
            this.loading = true;
            try {
                const response = await fetch(`https://babawallet.alwaysdata.net/api/common/notifications/page?page=${this.nbr}&limit=3`, requestOptions);
                if (!response.ok) {
                    if (response.status == 401) {
                        throw new Error("Token");
                    } else {
                        const data = await response.json();
                        throw new Error(data.error);
                    }
                } else {
                    const data = await response.json();
                    this.lastPage = data.last_page;
                    if (this.lastPage == 0) {
                        this.loading = true;
                        Swal.fire(this.$t("alerts.nonotification"));
                    } else if(this.lastPage >= this.nbr) {
                        this.id = data.id_proposal;
                        this.notifications = data.allNotifications;
                        this.notifications = this.notifications.flat();
                        this.loading = false;
                    }
                }
            } catch (error) {
                if (error.message === "Token") {
                    GlobalMethods.errorToken();
                } else {
                    GlobalMethods.errorApi(error.message);
                }
            }
        },
        async acceptNotification(id_notification, ean, address) {
            if(ean.length === 18 || this.role === "supplier") {
                const requestOptions = {
                    method: "POST",
                    headers: {'Authorization': this.$cookies.get("token")},
                    body: JSON.stringify({
                        ean: ean,
                        address: address,
                    })
                }
                console.log(ean);
                console.log(address);
                fetch('https://babawallet.alwaysdata.net/api/common/notifications/accept_notification/' + id_notification, requestOptions)
                    .then(response => {
                        if (!response.ok) {
                            if (response.status == 401) {
                                throw new Error("Token");
                            } else {
                                const data = response.json();
                                throw new Error(data.error);
                            }
                        } else {
                            Swal.fire(this.$t("alerts.notificationaccepted"));
                            this.refreshNotifications();
                        }
                    })
                    .catch(error => {
                        if (error.message === "Token") {
                            GlobalMethods.errorToken();
                        } else {
                            GlobalMethods.errorApi(error.message);
                        }
                    });
            } else {
                Swal.fire(this.$t("alerts.wrongean"));
            }
            await this.refreshNotifications();
        },
        async refuseNotification(id_notification) {
            const requestOptions = {
                method: "POST",
                headers: {'Authorization': this.$cookies.get("token")},
            }
            fetch("https://babawallet.alwaysdata.net/api/common/notifications/refuse_notification/" + id_notification, requestOptions)
                .then(response => {
                    if (!response.ok) {
                        if (response.status == 401) {
                            throw new Error("Token");
                        } else {
                            const data = response.json();
                            throw new Error(data.error);
                        }
                    } else {
                        Swal.fire(this.$t("alerts.refusednotification"));
                        this.refreshNotifications();
                    }
                })
                .catch(error => {
                    if (error.message === "Token") {
                        GlobalMethods.errorToken();
                    } else {
                        GlobalMethods.errorApi(error.message);
                    }
                });
        },
        convertLocation: function(location) {
            const result = [];

            if (location >= 100) {
                result.push(this.$t("proposal.wallonia"));
                location -= 100;
            }

            if (location >= 10) {
                result.push(this.$t("proposal.flanders"));
                location -= 10;
            }

            if (location >= 1) {
                result.push(this.$t("proposal.brussels"));
            }

            return result.join(' - ');
        },
        redirecting() {
            GlobalMethods.isAClient();
        },
    },
};
</script>

<style scoped>

.main {
    display: flex;
    flex-direction: column;
    justify-content: space-evenly;
    height: 100vh;
}

.header {
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 9999;
}

.notifs {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: space-evenly;
    overflow-y: scroll;
}

.notif {
    margin: 5px;
}

.bottombuttons {
    display: flex;
    justify-content: space-evenly;
    align-items: center;
    margin: 10px;
}
</style>