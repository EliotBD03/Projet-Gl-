<template>
    <div class="main">
        <div class="header">
            <MainHeader :text="contract.name_proposal" />
        </div>
        <div class="informations">
            <p>
                Type of energy : {{ contract.type_of_energy }}
            </p>
            <p>
                Location : {{ contract.localization }}
            </p>
            <p>
                Basic price : {{ contract.basic_price }}
            </p>
            <p>
                Price per day : {{ contract.variable_day_price }}
            </p>
            <p>
                Price per night : {{ contract.variable_night_price }}
            </p>
            <p>
                Rate : {{ contract.rate }}
            </p>
            <p>
                Start peak hour : {{ contract.start_off_peak_hours }}
            </p>
            <p>
                End peak hour : {{ contract.end_off_peak_hours }}
            </p>
            <p>
                Counter : {{ contract.counter }}
            </p>
        </div>
        <div class="bottombuttons">
            <div class="backbutton" @click.prevent.left="back()">
                <GoButton text="Back" :colore="'darkblue'"/>
            </div>
            <div class="changebutton" @click.prevent.left="$router.push({name: 'ChangeProposal'})">
                <GoButton text="Change proposal" :colore="'#34c98e'"/>
            </div>
            <div class="closebutton" @click.prevent.left="deleteProposal()">
                <GoButton text="Close the proposal" :colore="'red'"/>
            </div>
        </div>
    </div>
</template>

<script>
import MainHeader from "@/components/MainHeader.vue";
import GlobalMethods from "@/components/GlobalMethods.vue";
import Swal from "sweetalert2";
import GoButton from "@/components/GoButton.vue";

export default {
    components: {
        MainHeader,
        GoButton
    },
    data() {
        return {
            name_proposal: sessionStorage.getItem('name_proposal'),
            contract: []
        }},
    async created() {
        const requestOptions = {
            method: 'GET',
            headers: {'Authorisation' : this.$cookies.get('token')}
        };
        try {
            const response = await fetch(`https://babawallet.alwaysdata.net/api/supplier/proposals/${this.name_proposal}`,requestOptions);
            if (response.ok) {
                const data = await response.text();
                if (response.status === 401 && data.trim() === ''){
                    throw new Error('Token');
                }
                else {
                    const data = await response.json();
                    throw new Error(data.error);
                }
            }
            else {
                const data = await response.json();
                this.contract = data.contract;
            }
        }
        catch(error) {
            if(error.message === 'Token') {
                this.$cookies.remove('token');
                this.$cookies.remove('role');
                Swal.fire('Your connection has expired');
                this.$router.push('/');
            }
            else {
                GlobalMethods.errorApi(error.message);
            }
        }
    },
    methods: {
        back() {
            sessionStorage.removeItem('name_proposal');
            this.$router.push({name: 'ContractsSupplier'});
        },
        deleteProposal(){
            const requestOptions = {
                method: 'DELETE',
                headers: {'Authorisation' : this.$cookies.get('token')}
            };
            fetch(`https://babawallet.alwaysdata.net/api/supplier/proposals/${this.name_proposal}`,requestOptions)
                .then(response => {
                    if(!response.ok){
                        const data = response.text();
                        if(response.status == 401 && data.trim() === ''){
                            throw new Error("Token");
                        }
                        else{
                            return response.json().then(json => Promise.reject(json));
                        }
                    }
                    else{
                        Swal.fire({
                            icon: 'success',
                            title: 'Good !',
                            text: 'Contract deleted !'
                        })
                        this.$router.push({name: 'Wallets'});
                    }
                })
                .catch(error => {
                    if(error.message === "Token") {
                        this.$cookies.remove("token");
                        this.$cookies.remove("role");
                        Swal.fire('Your connection has expired');
                        this.$router.push("/");
                    }
                    else {
                        GlobalMethods.errorApi(error.error);
                    }
                });
        },
    }
};
</script>

<style scoped>

.main {
    display: flex;
    flex-direction: column;
    justify-content: space-evenly;
    align-items: center;
    height: 100vh;
}

.informations {
    display: flex;
    align-items: center;
    justify-content: center;
    flex-direction: column;
    width: 600px;
    height: 550px;
    border-radius: 50px;
    background: #e0e0e0;
    box-shadow: 0 15px 50px rgba(177, 185, 252, 1);
}

.bottombuttons {
    display: flex;
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
    width: 95%;
    padding: 0 50px;
    margin-top: 50px;
}

</style>